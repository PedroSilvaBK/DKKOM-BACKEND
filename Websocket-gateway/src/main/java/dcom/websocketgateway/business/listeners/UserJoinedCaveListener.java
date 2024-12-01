package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.Response;
import dcom.websocketgateway.domain.RoleAssignedToMember;
import dcom.websocketgateway.domain.UserJoinedCave;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserJoinedCaveListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;

    @KafkaListener(topics = "user-joined-cave", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            UserJoinedCave userJoinedCave = objectMapper.readValue(record.value(), UserJoinedCave.class);

            Set<WebSocketSession> sessions = sessionService.getSessionsForChannels(userJoinedCave.getChannelsUserIsVisible());
            log.debug("Sessions retrieved for channelIds - {}", sessions.size());

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    String message = objectMapper.writeValueAsString(
                            Response.<UserJoinedCave>builder()
                                    .type(EventResponseType.USER_JOINED_CAVE.toString())
                                    .data(userJoinedCave)
                                    .build()
                    );

                    session.sendMessage(new TextMessage(message));
                    log.debug("Message sent to session sessionId - {}", session.getId());
                } else {
                    log.warn("Session is closed sessionId - {}  ", session.getId());
                }
            }

        } catch (Exception e) {
            log.error("Error processing message record - {} | error - {}",record.value(),e.getMessage(), e);
        }
    }

}
