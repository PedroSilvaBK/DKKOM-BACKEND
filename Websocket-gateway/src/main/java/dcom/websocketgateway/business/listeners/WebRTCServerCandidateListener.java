package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.VoiceService;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.Response;
import dcom.websocketgateway.domain.SessionDescription;
import dcom.websocketgateway.domain.WebRTCServerOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebRTCServerCandidateListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final VoiceService voiceService;

    @KafkaListener(topics = "webrtc-server-candidates", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            ServerCandidate serverCandidate = objectMapper.readValue(record.value(), ServerCandidate.class);
            String sessionId = voiceService.getUserSession(serverCandidate.getRoomId(), serverCandidate.getUserId());

            if (sessionId == null) {
                return;
            }

            WebSocketSession session = sessionService.getSession(sessionId);

            if (session.isOpen())
            {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Response.<Candidate>builder()
                                .type(EventResponseType.WEB_RTC_SERVER_CANDIDATE.toString())
                                .data(serverCandidate.getCandidate())
                                .build()
                )));
            }

        } catch (Exception e) {
            log.error("Error processing message record - {} | error - {}",record.value(),e.getMessage(), e);
        }
    }

}
