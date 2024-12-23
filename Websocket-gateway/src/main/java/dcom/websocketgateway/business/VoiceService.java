package dcom.websocketgateway.business;

import dcom.websocketgateway.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceService {
    @Value("${sfu-service-host}")
    private String sfuHost;
    private Map<String, RoomMetadata> rooms = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SessionDescription createConnection(CreatePeerRequest request, WebSocketSession session) {
        CurrentCaveInfo currentCaveInfo = (CurrentCaveInfo) session.getAttributes().get("currentCave");

        String url = sfuHost + "/connect";
        CreateConnectionResponse response = restTemplate.postForObject(url, request, CreateConnectionResponse.class);

        if (response == null) {
            throw new RuntimeException("Could not create connection");
        }

        if (response.getInstanceIP() == null) {
            throw new RuntimeException("Could not create connection");
        }

        log.info("Received response with server ip: ${}", response.getInstanceIP());

        String userId = session.getHandshakeHeaders().getFirst("x-user-id");

        RoomMetadata roomMetadata = rooms.get(request.getRoomId());
        if (roomMetadata == null) {
            Map<String, String> userSession = new HashMap<>();
            userSession.put(userId, session.getId());
            rooms.put(request.getRoomId(), RoomMetadata.builder()
                            .SFUAddr(response.getInstanceIP())
                            .id(request.getRoomId())
                            .usersSessions(userSession)
                    .build());
        }
        else {
            roomMetadata.getUsersSessions().put(userId, session.getId());
        }

        kafkaTemplate.send("user-joined-voice-channel-preprocess", UserJoinedVoiceChannelEvent.builder()
                .roomId(request.getRoomId())
                .userId(userId)
                .caveId(currentCaveInfo.getCaveId())
                .build());

        return response.getSessionDescription();
    }

    public void disconnect(String userId, String roomId) {
        RoomMetadata roomMetadata = rooms.get(roomId);

        String fullUrl = buildInstanceURL(roomMetadata.getSFUAddr()) + "/disconnect";

        ResponseEntity<Void> response = restTemplate.postForEntity(fullUrl, DisconnectRequest.builder()
                        .roomId(roomId)
                        .userId(userId)
                .build(), Void.class);

        if (response.getStatusCode().is2xxSuccessful()){
            rooms.get(roomId).getUsersSessions().remove(userId);
        }
    }

    public void createAnswer(CreatePeerRequest request) {
        RoomMetadata roomMetadata = rooms.get(request.getRoomId());

        String fullUrl = buildInstanceURL(roomMetadata.getSFUAddr()) + "/answer";


        restTemplate.postForObject(fullUrl, request, SessionDescription.class);
    }

    public String getUserSession(String roomId, String userId) {
        return rooms.get(roomId).getUsersSessions().get(userId);
    }

    public Collection<String> getUserSessions(String roomId) {
        if (rooms.get(roomId) == null) {
            return Collections.emptyList();
        }
        return rooms.get(roomId).getUsersSessions().values();
    }

    public void iceCandidate(ICECandidateRequest iceCandidateRequest) {
        log.info("ice candidate reached");
        RoomMetadata roomMetadata = rooms.get(iceCandidateRequest.getRoomId());

        log.info("Room Data found in ice candidate ${}", roomMetadata);

        String fullUrl = buildInstanceURL(roomMetadata.getSFUAddr()) + "/candidate";
        restTemplate.postForObject(fullUrl, iceCandidateRequest, Void.class);

        log.info("Request sent");
    }

    private String buildInstanceURL(String instanceIP){
        return "http://" + instanceIP + ":9999";
    }
}
