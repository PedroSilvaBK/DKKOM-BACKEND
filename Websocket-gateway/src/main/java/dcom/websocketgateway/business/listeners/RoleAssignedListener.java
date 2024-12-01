package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.utils.PermissionsUtils;
import dcom.websocketgateway.domain.*;
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
public class RoleAssignedListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;

    @KafkaListener(topics = "role-assigned-to-member", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            RoleAssignedToMember roleAssignedToMember = objectMapper.readValue(record.value(), RoleAssignedToMember.class);

            Set<WebSocketSession> sessions = sessionService.getSessionsForChannels(roleAssignedToMember.getChannelsSideListsWhereUserAppears());
            log.debug("Sessions retrieved for caveIds - {}", sessions.size());

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    String message = objectMapper.writeValueAsString(
                            Response.<RoleAssignedToMember>builder()
                                    .type(EventResponseType.ROLE_ASSIGNED_TO_MEMBER.toString())
                                    .data(roleAssignedToMember)
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
