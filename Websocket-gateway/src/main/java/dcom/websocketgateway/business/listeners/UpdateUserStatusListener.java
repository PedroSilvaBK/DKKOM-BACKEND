package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.clients.cave_service_client.CaveServiceClient;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.Response;
import dcom.websocketgateway.domain.UserPresence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserStatusListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final CaveServiceClient caveServiceClient;

    @KafkaListener(topics = "user-status-update", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            UserPresence userPresence = objectMapper.readValue(record.value(), UserPresence.class);

            log.info("Received user-status-update userId - {} : status - {}", userPresence.getUserId(), userPresence.getStatus());

            List<String> userCaves = caveServiceClient.getUserCaveIds(userPresence.getUserId());
            if (userCaves == null || userCaves.isEmpty()) {
                log.debug("No caves found for user userId - {} | caveValue {}", userPresence.getUserId(), userCaves);
                return;
            }

            log.debug("Caves retrieved for user userId - {} | caveIds - {}", userPresence.getUserId(), userCaves);

            Set<WebSocketSession> sessions = sessionService.getSessionsByCaveIds(userCaves);
            log.debug("Sessions retrieved for caveIds - {}", sessions.size());

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    String message = objectMapper.writeValueAsString(
                            Response.<UserPresence>builder()
                                    .type(EventResponseType.UPDATE_USER_PRESENCE.toString())
                                    .data(userPresence)
                                    .build()
                    );

                    session.sendMessage(new TextMessage(message));
                    log.debug("Message sent to session sessionId - {} | userId - {}", session.getId(), userPresence.getUserId());
                } else {
                    log.warn("Session is closed sessionId - {} | userId - {}", session.getId(), userPresence.getUserId());
                }
            }

        } catch (Exception e) {
            log.error("Error processing message record - {} | error - {}",record.value(),e.getMessage(), e);
        }
    }

}
