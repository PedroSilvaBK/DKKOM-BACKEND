package dcom.messaging_service.business.clients.permissions_service_client;

public interface PermissionServiceClient {
    boolean canSeeChannel(String userId, String channelId);
    boolean canSendMessage(String userId, String channelId);
}
