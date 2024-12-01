package dcom.gateway.api_gateway.configuration;

import dcom.gateway.api_gateway.configuration.filters.MessageServiceFilter;
import dcom.gateway.api_gateway.configuration.filters.WebSocketsFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    @Value("${user.service.host}")
    private String userServiceHost;

    @Value("${cave.service.host}")
    private String caveServiceHost;

    @Value("${websockets.gateway.ws.host}")
    private String wsGatewayWS;

    @Value("${websockets.gateway.http.host}")
    private String wsGatewayHTTP;
    @Value("${message.service.host}")
    private String messageServiceHost;

    @Value("${media.service.host}")
    private String mediaServiceHost;

    private final WebSocketsFilter webSocketAuthFilter;
    private final MessageServiceFilter messageServiceFilter;
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user", r -> r.path("/user-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(userServiceHost))
                .route("cave", r -> r.path("/cave-service/**")
                        .filters(f -> f.filter(messageServiceFilter).stripPrefix(1))
                        .uri(caveServiceHost))
                .route("message-service", r -> r.path("/message-service/**")
                        .filters(f -> f.filter(messageServiceFilter).stripPrefix(1))
                        .uri(messageServiceHost)
                )
                .route("media-service", r -> r.path("/media-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(mediaServiceHost)
                )
                .route("websocket_route", r -> r
                        .path("/ws/cave")
                        .filters(f -> f.filter(webSocketAuthFilter))// Path for WebSocket connections
                        .uri(wsGatewayWS))  // URI of your WebSocket server
                .route("websocket_auth_route", r-> r
                        .path("/ws/auth")
                        .filters(f -> f.stripPrefix(1))
                        .uri(wsGatewayHTTP)
                )
                .build();
    }
}
