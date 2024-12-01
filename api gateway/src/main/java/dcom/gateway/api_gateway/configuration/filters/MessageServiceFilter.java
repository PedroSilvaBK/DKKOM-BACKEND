package dcom.gateway.api_gateway.configuration.filters;

import dcom.gateway.api_gateway.configuration.jwt_token.RsaKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageServiceFilter implements GatewayFilter {
    private final RsaKeyProvider rsaKeyProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.debug("Request hit message filter with the header {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Claims claims = decodeJWT(token);

            log.debug("Token is present");

            if (claims != null) {
                String userId = claims.get("id", String.class); // e.g., userId
                String username = claims.get("username", String.class); // e.g., userId

                log.debug("Username is present - {}", username);
                log.debug("Userid is present - {}", userId);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
                        .build();

                // Create a new exchange with the modified request
                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
                return chain.filter(modifiedExchange);
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Claims decodeJWT(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(rsaKeyProvider.getPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("An error happened while decoding the jwt - {}", e.getMessage());
            return null;
        }
    }
}
