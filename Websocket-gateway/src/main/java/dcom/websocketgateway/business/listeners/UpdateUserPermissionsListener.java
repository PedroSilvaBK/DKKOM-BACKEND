package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.domain.CurrentCaveInfo;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserPermissionsListener {
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    private static final String UPDATE_USER_TOPIC = "update-user-permissions";
    private static final String UPDATE_USERS_TOPIC = "update-users-permissions";

    @KafkaListener(topics = {UPDATE_USER_TOPIC, UPDATE_USERS_TOPIC}, groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            HashMap<String, UserRolesAndPermissionsCache> updatedUserPermissions = objectMapper.readValue(record.value(), new TypeReference<HashMap<String, UserRolesAndPermissionsCache>>() {
            });

            for (Map.Entry<String, UserRolesAndPermissionsCache> entry : updatedUserPermissions.entrySet()) {
                // Extract caveId and userId from the key
                String[] keyParts = entry.getKey().split(":");
                String caveId = keyParts[0];
                String userId = keyParts[1];

                // Get all sessions in the cave
                Set<WebSocketSession> activeCaveSessions = sessionService.getSessionsByCave(caveId);
                for (WebSocketSession session : activeCaveSessions) {
                    // Ensure the session belongs to the correct user
                    String sessionUserId = session.getAttributes().get("userId").toString();
                    if (session.isOpen() && sessionUserId.equals(userId)) {
                        UserRolesAndPermissionsCache userRolesAndPermissionsCache = entry.getValue();

                        CurrentCaveInfo currentCaveInfo = (CurrentCaveInfo) session.getAttributes().get("currentCave");
                        if (currentCaveInfo != null) {
                            currentCaveInfo.setUserRoleIds(userRolesAndPermissionsCache.getUserRoles());
                            currentCaveInfo.setChannelsWithOverriddenPermissions(
                                    userRolesAndPermissionsCache.getChannelPermissionsCacheHashMap()
                                            .keySet().stream().toList()
                            );
                            currentCaveInfo.setUserRolesAndPermissionsCache(userRolesAndPermissionsCache);

                            session.getAttributes().put("currentCave", currentCaveInfo);
                        }

                        // Send the update to the correct user session
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                                Response.<UserRolesAndPermissionsCache>builder()
                                        .type(EventResponseType.UPDATE_USER_PERMISSION.toString())
                                        .data(userRolesAndPermissionsCache)
                                        .build()
                        )));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
