package dcom.websocketgateway.business.clients.cave_service_client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaveServiceClientImpl implements CaveServiceClient{
    private final RestTemplate restTemplate;

    @Value("${cave-service-host}")
    private String caveServiceUrl;

    public List<String> getUserCaveIds(String userId) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(caveServiceUrl)
                .path("cave")
                .path("/")
                .path("user")
                .path("/")
                .path(userId);

        return restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {}).getBody();
    }
}
