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
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketsFilter implements GatewayFilter {

    private final RsaKeyProvider rsaKeyProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authToken = exchange.getRequest().getQueryParams().getFirst("token");

        log.debug("Auth token reached websockets filter - {}", authToken);
        if (authToken != null) {
            Claims claims = decodeJWT(authToken);

            log.debug("token decoded successfully");

            if (claims != null) {
                String userId = claims.get("id", String.class); // e.g., userId

                ServerHttpRequest modifiedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.putAll(super.getHeaders());
//                        headers.remove(HttpHeaders.AUTHORIZATION); // Remove the Authorization header
                        headers.add("X-User-Id", userId);
                        return headers;
                    }
                };

                log.debug("Request modified");

                exchange = exchange.mutate().request(modifiedRequest).build();
                return chain.filter(exchange);
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
            log.error("Something went wrong while decoding token on WebSocket filter - {}",e.getMessage());
            return null;
        }
    }
}
