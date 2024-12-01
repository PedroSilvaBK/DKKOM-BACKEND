package dcom.websocketgateway.business.clients.permission_service_client;

import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PermissionServiceClientImpl implements PermissionsServiceClient{
    private final RestTemplate restTemplate;

    @Value("${permissions-service-host}")
    private String permissionServiceUrl;

    public UserRolesAndPermissionsCache getUserRolesAndPermissionsCacheByChannel(String userId, String channelId) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(permissionServiceUrl)
                .path("permissions")
                .path("/")
                .path(userId)
                .path("/")
                .path("channel")
                .path("/")
                .path(channelId);

        return restTemplate.getForObject(uriBuilder.toUriString(), UserRolesAndPermissionsCache.class);
    }

    public UserRolesAndPermissionsCache getUserRolesAndPermissionsCache(String userId, String caveId) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(permissionServiceUrl)
                .path("permissions")
                .path("/")
                .path(userId)
                .path("/")
                .path("cave")
                .path("/")
                .path(caveId);

        return restTemplate.getForObject(uriBuilder.toUriString(), UserRolesAndPermissionsCache.class);
    }

    public Boolean caveExists(String userId, String caveId) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(permissionServiceUrl)
                .path("permissions")
                .path("/")
                .path(userId)
                .path("/")
                .path("cave")
                .path("/")
                .path(caveId)
                .path("/")
                .path("exists");

        return restTemplate.getForObject(uriBuilder.toUriString(), Boolean.class);
    }
}
