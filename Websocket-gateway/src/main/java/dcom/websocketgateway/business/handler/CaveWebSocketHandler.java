package dcom.websocketgateway.business.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.clients.permission_service_client.PermissionsServiceClient;
import dcom.websocketgateway.business.user_status.UserStatusService;
import dcom.websocketgateway.business.utils.PermissionsUtils;
import dcom.websocketgateway.domain.CurrentCaveInfo;
import dcom.websocketgateway.domain.Event;
import dcom.websocketgateway.domain.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaveWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;

    private final PermissionsServiceClient permissionsServiceClient;
    private final PermissionsUtils permissionsUtils;

    private final SessionService sessionService;
    private final UserStatusService userStatusService;

    private final static String USER_ID_HEADER = "x-user-id";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = session.getHandshakeHeaders().getFirst(USER_ID_HEADER);
        sessionService.saveSession(session.getId(), session);

        log.debug("User - {} connected", userId);

        userStatusService.updateUserStatus(userId, Status.ONLINE);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = session.getHandshakeHeaders().getFirst(USER_ID_HEADER);
        sessionService.deleteSession(session.getId());

        log.debug("User - {} disconnected", userId);

        userStatusService.updateUserStatus(userId, Status.OFFLINE);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Event event = objectMapper.readValue(message.getPayload(), Event.class);

        log.debug("Received event {}", event.getType());
        switch (event.getType())
        {
            case "ping":
                break;
            case "select_cave":
                selectCave(event, session);
                break;
            case "subscribe_channel":
                subscribeToChannel(event, session);
                break;
            default:
                log.warn("Unhandled event {}", event.getType());
                throw new IllegalStateException("Unexpected value: " + event.getType());
        }
    }

    private void subscribeToChannel(Event event, WebSocketSession session) {
        String channelId = (String) event.getProperties().get("channelId");
        log.info("Subscribing to channel {}", channelId);
        String userId = session.getHandshakeHeaders().getFirst(USER_ID_HEADER);

        UserRolesAndPermissionsCache permissions = permissionsServiceClient.getUserRolesAndPermissionsCacheByChannel(userId, channelId);

        if (!permissionsUtils.canSeeChannel(permissions, channelId)) {
            log.debug("user - {} cannot see channel - {}", userId, channelId);
            return;
        }

        sessionService.subscribeToChannel(channelId, session);

        log.info("Subscribed to channel {}", channelId);
    }

    private void selectCave(Event event, WebSocketSession session){
        String userId = session.getHandshakeHeaders().getFirst(USER_ID_HEADER);
        String caveId = event.getProperties().get("caveId").toString();

        Object userCaveRoleIds = event.getProperties().get("userCaveRoleIds");
        Object channelsWithOverriddenPermissions =event.getProperties().get("channelsWithOverriddenPermissions");

        if (!(userCaveRoleIds instanceof List<?>)) {
            return;
        }

        if (!(channelsWithOverriddenPermissions instanceof List<?>)) {
            return;
        }

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = permissionsServiceClient.getUserRolesAndPermissionsCache(userId, caveId);

        Boolean belongsToTheCave = permissionsServiceClient.caveExists(userId, caveId);

        if (Boolean.FALSE.equals(belongsToTheCave)) {
            log.debug("User - {} doesnt belong  to cave - {}", userId, caveId);
            return;
        }

        session.getAttributes().put("userId", userId);
        session.getAttributes().put("currentCave",
                CurrentCaveInfo.builder()
                        .channelsWithOverriddenPermissions((List<String>) channelsWithOverriddenPermissions)
                        .userRoleIds((List<String>) userCaveRoleIds)
                        .caveId(caveId)
                        .userRolesAndPermissionsCache(userRolesAndPermissionsCache)
                        .build()
        );

        sessionService.addCaveSession(caveId, session);
    }
}
