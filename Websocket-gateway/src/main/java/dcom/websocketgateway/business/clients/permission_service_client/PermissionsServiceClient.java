package dcom.websocketgateway.business.clients.permission_service_client;

import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;

public interface PermissionsServiceClient {
    UserRolesAndPermissionsCache getUserRolesAndPermissionsCacheByChannel(String userId, String channelId);
    Boolean caveExists(String userId, String caveId);
    UserRolesAndPermissionsCache getUserRolesAndPermissionsCache(String userId, String caveId);
}
