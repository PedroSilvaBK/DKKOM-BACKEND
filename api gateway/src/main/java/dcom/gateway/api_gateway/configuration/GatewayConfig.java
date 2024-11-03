package dcom.gateway.api_gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Value("${user.service.host}")
    private String userServiceHost;

    @Value("${cave.service.host}")
    private String caveServiceHost;
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user", r -> r.path("/user-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(userServiceHost))
                .route("cave", r -> r.path("/cave-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(caveServiceHost))
                .route("websocket_route", r -> r
                        .path("/ws/cave")  // Path for WebSocket connections
                        .uri("ws://localhost:8087/ws/cave"))  // URI of your WebSocket server
                .build();
    }
}
