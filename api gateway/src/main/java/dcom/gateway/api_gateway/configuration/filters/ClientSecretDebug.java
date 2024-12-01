package dcom.gateway.api_gateway.configuration.filters;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientSecretDebug {
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @PostConstruct
    public void debugClientSecret() {
        System.out.println("Loaded Google Client Secret: " + clientSecret);
    }
}
