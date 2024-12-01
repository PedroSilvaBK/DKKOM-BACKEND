package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.clients.cave_service_client.CaveServiceClient;
import dcom.websocketgateway.business.utils.PermissionsUtils;
import dcom.websocketgateway.domain.*;
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
public class CaveRoleCreatedListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final PermissionsUtils permissionsUtils;

    @KafkaListener(topics = "cave-role-created", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            CaveRoleCreated caveRoleCreated = objectMapper.readValue(record.value(), CaveRoleCreated.class);

            Set<WebSocketSession> sessions = sessionService.getSessionsByCave(caveRoleCreated.getCaveId().toString());
            log.debug("Sessions retrieved for caveIds - {}", sessions.size());

            for (WebSocketSession session : sessions) {
                UserRolesAndPermissionsCache userRolesAndPermissionsCache = ((CurrentCaveInfo) session.getAttributes().get("currentCave")).getUserRolesAndPermissionsCache();
                if (permissionsUtils.canManageRoles(userRolesAndPermissionsCache) && session.isOpen()) {
                    String message = objectMapper.writeValueAsString(
                            Response.<CaveRoleCreated>builder()
                                    .type(EventResponseType.CAVE_ROLE_CREATED.toString())
                                    .data(caveRoleCreated)
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
