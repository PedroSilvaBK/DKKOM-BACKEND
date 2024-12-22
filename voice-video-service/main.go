package main

import (
	"encoding/json"
	"fmt"
	"github.com/IBM/sarama"
	"github.com/gorilla/websocket"
	"github.com/pion/webrtc/v3"
	"github.com/rs/cors"
	"log"
	"net/http"
	"os"
	"sync"
)

type threadSafeWriter struct {
	*websocket.Conn
	sync.Mutex
}

type Peer struct {
	ID   string
	Conn *threadSafeWriter
	PC   *webrtc.PeerConnection
}

type Room struct {
	Peers  map[string]*Peer
	Tracks map[string]*webrtc.TrackLocalStaticRTP
	Mutex  sync.Mutex
}

type CreatePeerRequest struct {
	UserId string                     `json:"user_id"`
	RoomId string                     `json:"room_id"`
	Offer  *webrtc.SessionDescription `json:"offer"`
}

type KafkaOffer struct {
	UserID string                     `json:"user_id"`
	RoomID string                     `json:"room_id"`
	Offer  *webrtc.SessionDescription `json:"offer"`
}

type ICECandidateRequest struct {
	UserID    string                   `json:"user_id"`
	RoomID    string                   `json:"room_id"`
	Candidate *webrtc.ICECandidateInit `json:"candidate"`
}

type CreateConnectionResponse struct {
	Answer     webrtc.SessionDescription `json:"sessionDescription"`
	InstanceIP string                    `json:"instanceIP"`
}

type DisconnectRequest struct {
	UserId string `json:"user_id"`
	RoomId string `json:"room_id"`
}

var (
	mu         sync.Mutex
	rooms      = make(map[string]*Room)
	producer   sarama.SyncProducer
	instanceIP string
)

func main() {
	instanceIP = os.Getenv("POD_IP")

	initKafkaProducer()

	http.HandleFunc("/connect", createPeerConnection)
	http.HandleFunc("/answer", handleAnswer)
	http.HandleFunc("/candidate", handleIceCandidate)
	http.HandleFunc("/disconnect", handleDisconnect)

	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"*"}, // Allow all origins. Replace "*" with specific domains if needed.
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Content-Type", "Authorization"},
		AllowCredentials: true,
	})

	handler := corsHandler.Handler(http.DefaultServeMux)

	log.Println("Simplified SFU server running on :9999")
	log.Fatal(http.ListenAndServe(":9999", handler))
}

func initKafkaProducer() {
	config := sarama.NewConfig()
	config.Producer.RequiredAcks = sarama.WaitForAll // Wait for all in-sync replicas to ack the message
	config.Producer.Retry.Max = 5                    // Retry up to 5 times
	config.Producer.Return.Successes = true

	var err error
	producer, err = sarama.NewSyncProducer([]string{"localhost:9092"}, config)
	if err != nil {
		log.Fatalf("Failed to start Kafka producer: %v", err)
	}

	log.Println("Kafka producer initialized")
}

func sendOfferToKafka(userID, roomID string, offer *webrtc.SessionDescription) {
	message := KafkaOffer{
		UserID: userID,
		RoomID: roomID,
		Offer:  offer,
	}

	offerJSON, err := json.Marshal(message)
	if err != nil {
		log.Printf("Error marshalling offer to JSON: %v", err)
		return
	}

	msg := &sarama.ProducerMessage{
		Topic: "webrtc-offers",
		Value: sarama.ByteEncoder(offerJSON),
	}

	partition, offset, err := producer.SendMessage(msg)
	if err != nil {
		log.Printf("Failed to send offer to Kafka: %v", err)
		return
	}

	log.Printf("Offer sent to Kafka topic 'webrtc-offers' | partition: %d, offset: %d", partition, offset)
}

func handleAnswer(w http.ResponseWriter, r *http.Request) {
	var answerRequest CreatePeerRequest

	err := json.NewDecoder(r.Body).Decode(&answerRequest)
	if err != nil {
		log.Printf("Error decoding answer: %v", err)
		return
	}

	peer := rooms[answerRequest.RoomId].Peers[answerRequest.UserId]

	// offer is the answer
	if answerRequest.Offer == nil {
		log.Println("Invalid answer received.")
		return
	}
	if err := peer.PC.SetRemoteDescription(*answerRequest.Offer); err != nil {
		log.Println("Error setting remote description:", err)
	}
}

func handleIceCandidate(w http.ResponseWriter, r *http.Request) {
	var candidateRequest ICECandidateRequest

	err := json.NewDecoder(r.Body).Decode(&candidateRequest)
	if err != nil {
		log.Printf("Error decoding cadidateRequest: %v", err)
		return
	}

	peer := rooms[candidateRequest.RoomID].Peers[candidateRequest.UserID]

	if candidateRequest.Candidate == nil {
		log.Println("Invalid candidate received.")
		return
	}
	if err := peer.PC.AddICECandidate(*candidateRequest.Candidate); err != nil {
		log.Println("Error adding ICE candidate:", err)
	}
}

func createPeerConnection(w http.ResponseWriter, r *http.Request) {
	var peerRequest CreatePeerRequest

	err := json.NewDecoder(r.Body).Decode(&peerRequest)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	peer := &Peer{
		ID: peerRequest.UserId,
	}

	peer.PC, err = webrtc.NewPeerConnection(webrtc.Configuration{
		ICEServers: []webrtc.ICEServer{
			{
				URLs: []string{"stun:stun.l.google.com:19302"},
			},
		},
	})

	addPeerToRoom(peer, peerRequest.RoomId)
	handleNewPeer(peer, peerRequest.RoomId)

	createConnectionResponse := &CreateConnectionResponse{
		InstanceIP: instanceIP,
	}

	answer := handleOffer(peer, peerRequest.Offer)

	createConnectionResponse.Answer = answer

	responseJson, _ := json.Marshal(createConnectionResponse)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	w.Write(responseJson)
}

func handleDisconnect(w http.ResponseWriter, r *http.Request) {
	var peerRequest DisconnectRequest

	err := json.NewDecoder(r.Body).Decode(&peerRequest)
	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	peer, exists := rooms[peerRequest.RoomId].Peers[peerRequest.UserId]
	if !exists {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	peer.PC.Close()

	removePeerTracksFromRoom(peer, peerRequest.RoomId)
	removePeerFromRoom(peerRequest.RoomId, peerRequest.UserId)

	w.WriteHeader(http.StatusNoContent)
}

func addPeerToRoom(peer *Peer, room string) {
	mu.Lock()
	defer mu.Unlock()

	if _, exists := rooms[room]; !exists {
		rooms[room] = &Room{
			Peers:  make(map[string]*Peer),
			Tracks: make(map[string]*webrtc.TrackLocalStaticRTP),
		}
	}

	rooms[room].Peers[peer.ID] = peer
	log.Printf("Added peer %s to rom %s\n", peer.ID, room)
}

func removePeerFromRoom(roomID, peerID string) {
	mu.Lock()

	room, exists := rooms[roomID]
	mu.Unlock()
	if !exists {
		return
	}

	room.Mutex.Lock()
	// Remove the peer
	delete(room.Peers, peerID)
	log.Printf("Removed peer %s from room %s", peerID, roomID)

	// Clean up room if empty
	if len(room.Peers) == 0 && len(room.Tracks) == 0 {
		room.Mutex.Unlock()
		delete(rooms, roomID)
		log.Printf("Deleted room %s as it is now empty", roomID)
		return
	}

	room.Mutex.Unlock()
}

func addTrackToRoom(roomID, trackID string, track *webrtc.TrackLocalStaticRTP) {
	mu.Lock()
	room, exists := rooms[roomID]
	mu.Unlock()

	room.Mutex.Lock()
	defer room.Mutex.Unlock()

	if !exists {
		log.Printf("Room %s does not exist, creating it", roomID)
		room = &Room{
			Peers:  make(map[string]*Peer),
			Tracks: make(map[string]*webrtc.TrackLocalStaticRTP),
		}
		rooms[roomID] = room
	}

	room.Tracks[trackID] = track
	log.Printf("Added track %s to room %s", trackID, roomID)
}

func removeTrackFromRoom(roomID, trackID string) {
	mu.Lock()
	room, exists := rooms[roomID]
	mu.Unlock()

	if !exists {
		return
	}

	room.Mutex.Lock()
	defer room.Mutex.Unlock()

	delete(room.Tracks, trackID)
	log.Printf("Removed track %s from room %s", trackID, roomID)

	// Clean up room if empty
	if len(room.Peers) == 0 && len(room.Tracks) == 0 {
		delete(rooms, roomID)
		log.Printf("Deleted room %s as it is now empty", roomID)
	}
}

func handleNewPeer(peer *Peer, roomId string) {
	mu.Lock()
	log.Printf("New peer %s connected.", peer.ID)
	room, exists := rooms[roomId]
	mu.Unlock()

	room.Mutex.Lock()
	defer room.Mutex.Unlock()

	if !exists {
		log.Printf("Room %s does not exist when adding tracks to peer in it", roomId)
		return
	}

	// Add existing tracks to the new peer
	for _, track := range room.Tracks {
		if _, err := peer.PC.AddTrack(track); err != nil {
			log.Printf("Error adding track to peer %s: %v", peer.ID, err)
		}
	}

	peer.PC.OnTrack(func(remoteTrack *webrtc.TrackRemote, receiver *webrtc.RTPReceiver) {
		log.Printf("Received track from peer %s: Kind=%s, ID=%s", peer.ID, remoteTrack.Kind(), remoteTrack.ID())

		localTrack, err := webrtc.NewTrackLocalStaticRTP(remoteTrack.Codec().RTPCodecCapability, remoteTrack.ID(), fmt.Sprintf("stream-%s", peer.ID))
		if err != nil {
			log.Printf("Error creating local track: %v", err)
			return
		}

		addTrackToRoom(roomId, remoteTrack.ID(), localTrack)

		log.Printf("Added track %s to trackLocals with StreamID %s", remoteTrack.ID(), remoteTrack.StreamID())

		go func() {
			buf := make([]byte, 1500)
			for {

				if remoteTrack == nil {
					log.Printf("Remote track is nil for track %s", remoteTrack.ID())
				}

				n, _, err := remoteTrack.Read(buf)
				if err != nil {
					log.Printf("Error reading track: %v", err)
					return
				}

				if localTrack == nil {
					log.Printf("Local track is nil for track %s", remoteTrack.ID())
				}

				if _, err = localTrack.Write(buf[:n]); err != nil {
					log.Printf("Error writing to local track: %v", err)
					return
				}
			}
		}()

		signalAllPeers(roomId)
	})
}

func removePeerTracksFromRoom(peer *Peer, roomId string) {
	mu.Lock()
	room, exists := rooms[roomId]
	mu.Unlock()
	if !exists {
		log.Printf("Room %s does not exist", roomId)
		return
	}

	room.Mutex.Lock()

	// Flag to determine if the room has been deleted
	roomDeleted := false

	for trackID, track := range room.Tracks {
		if track.StreamID() == fmt.Sprintf("stream-%s", peer.ID) {
			delete(room.Tracks, trackID)
			log.Printf("Removed track %s from room %s", trackID, roomId)

			// Clean up room if empty
			if len(room.Peers) == 0 && len(room.Tracks) == 0 {
				roomDeleted = true
				break
			}
		}
	}

	// If the room was deleted, clean it up and exit early.
	room.Mutex.Unlock()
	if roomDeleted {
		mu.Lock()
		delete(rooms, roomId)
		mu.Unlock()
		log.Printf("Deleted room %s as it is now empty", roomId)
		return // Exit early to prevent further actions.
	}

	// Signal peers only if the room still exists.
	signalAllPeers(roomId)
}

func signalAllPeers(roomId string) {

	if room, exists := rooms[roomId]; exists {

		for _, peerState := range room.Peers {

			existingSenders := map[string]bool{}

			// Check existing senders
			for _, sender := range peerState.PC.GetSenders() {
				if sender.Track() != nil {
					existingSenders[sender.Track().ID()] = true

					// Remove tracks that are no longer in trackLocals
					if _, exists := room.Tracks[sender.Track().ID()]; !exists {
						// Ensure PeerConnection is stable before removing
						if peerState.PC.SignalingState() != webrtc.SignalingStateStable {
							log.Printf("Cannot remove track %s from peer %s: PeerConnection is not stable", sender.Track().ID(), peerState.ID)
							continue
						}

						// Attempt to remove the track
						if err := peerState.PC.RemoveTrack(sender); err != nil {
							log.Printf("Error removing track from sender: %v", err)
						} else {
							log.Printf("Removed track from peer %s", peerState.ID)
						}
					}
				} else {
					log.Printf("Sender track is nil for peer %s, skipping removal", peerState.ID)
				}
			}

			// Add tracks from trackLocals, excluding the peer's own tracks
			for trackID, localTrack := range room.Tracks {
				if localTrack.StreamID() == fmt.Sprintf("stream-%s", peerState.ID) {
					log.Printf("Skipping own track %s for peer %s", trackID, peerState.ID)
					continue
				}

				if _, alreadySending := existingSenders[trackID]; !alreadySending {
					if _, err := peerState.PC.AddTrack(localTrack); err != nil {
						log.Printf("Error adding track %s to peer %s: %v", trackID, peerState.ID, err)
					} else {
						log.Printf("Added track %s to peer %s", trackID, peerState.ID)
					}
				}
			}

			// Create and send a new offer
			offer, err := peerState.PC.CreateOffer(nil)
			if err != nil {
				log.Printf("Error creating offer: %v", err)
				continue
			}

			if err := peerState.PC.SetLocalDescription(offer); err != nil {
				log.Printf("Error setting local description: %v", err)
				continue
			}

			// Send the updated offer
			// Check if the connection is still open
			sendOfferToKafka(peerState.ID, roomId, &offer)
			//err = peerState.Conn.WriteJSON(Message{
			//	Type:  "offer",
			//	Offer: &offer,
			//})
			if err != nil {
				log.Printf("Error sending offer to peer %s: %v. Removing peer.", peerState.ID, err)

				// Safely remove the disconnected peer
				//mu.Lock()
				removePeerFromRoom(roomId, peerState.ID)
				//delete(peers, peerState.ID)
				//mu.Unlock()
				// Close the PeerConnection if it's still active
				if peerState.PC.ConnectionState() != webrtc.PeerConnectionStateClosed {
					if closeErr := peerState.PC.Close(); closeErr != nil {
						log.Printf("Error closing PeerConnection for peer %s: %v", peerState.ID, closeErr)
					}
				}
			} else {
				log.Printf("Sent offer to peer %s", peerState.ID)
			}

		}
	}

}

func handleOffer(peer *Peer, offer *webrtc.SessionDescription) webrtc.SessionDescription {
	if offer == nil {
		log.Println("Invalid offer received.")
		return webrtc.SessionDescription{}
	}
	if err := peer.PC.SetRemoteDescription(*offer); err != nil {
		log.Println("Error setting remote description:", err)
		return webrtc.SessionDescription{}
	}

	answer, err := peer.PC.CreateAnswer(nil)
	if err != nil {
		log.Println("Error creating answer:", err)
		return webrtc.SessionDescription{}
	}
	if err := peer.PC.SetLocalDescription(answer); err != nil {
		log.Println("Error setting local description:", err)
		return webrtc.SessionDescription{}
	}
	//peer.Conn.WriteJSON(Message{Type: "answer", Answer: &answer})
	return answer
}

func (t *threadSafeWriter) WriteJSON(v interface{}) error {
	t.Lock()
	defer t.Unlock()

	return t.Conn.WriteJSON(v)
}
