package dcom.messaging_service.business.clients.permissions_service_client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceClientImpl implements PermissionServiceClient {
    @Value("${permission.service.url}")
    private String permissionServiceUrl;
    private final RestTemplate restTemplate;

    public boolean canSeeChannel(String userId, String channelId) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(permissionServiceUrl)
                .path("permissions")
                .path("/")
                .path(userId)
                .path("/")
                .path("channel")
                .path("/")
                .path(channelId)
                .path("/")
                .path("see-channel");

        Boolean canSeeChannel = restTemplate.getForObject(uriBuilder.toUriString(), Boolean.class);

        log.info("canSeeChannel was invoked for user - {} | canSeeChannel:{}", userId, canSeeChannel);

        return Boolean.TRUE.equals(canSeeChannel);
    }

    public boolean canSendMessage(String userId, String channelId) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(permissionServiceUrl)
                .path("permissions")
                .path("/")
                .path(userId)
                .path("/")
                .path("channel")
                .path("/")
                .path(channelId)
                .path("/")
                .path("send-messages");

        Boolean canSendMessage = restTemplate.getForObject(uriBuilder.toUriString(), Boolean.class);

        log.info("canSendMessage was invoked for user - {} | canSendMessage:{}", userId, canSendMessage);

        return Boolean.TRUE.equals(canSendMessage);
    }

}
