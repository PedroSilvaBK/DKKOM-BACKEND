package dcom.gateway.api_gateway.configuration.filters;

import dcom.gateway.api_gateway.configuration.jwt_token.JwtTokenProvider;
import dcom.gateway.api_gateway.domain.RegisterUserRequest;
import dcom.gateway.api_gateway.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuccessOauthFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${frontend.auth.callback.url}")
    private String callbackUrl;

    @Value("${user.service.host}")
    private String userServiceUrl;

    @Value("${prod.cookie}")
    private boolean prodCookie;


    @Bean
    public ServerAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            String email = oauthToken.getPrincipal().getAttribute("email");
            String name = oauthToken.getPrincipal().getAttribute("name");

            RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                    .name(name)
                    .email(email)
                    .build();

            log.debug("Reached Filter {} | {}", email, registerUserRequest);

            WebClient webClient = WebClient.builder().baseUrl(userServiceUrl).build();


            log.debug("callbackUrl {}", callbackUrl);
            return webClient.post()
                    .uri("/user/register")
                    .body(BodyInserters.fromValue(registerUserRequest))
                    .retrieve()
                    .bodyToMono(User.class)
                    .flatMap(userDetails -> {
                        log.debug("request success");
                        String applicationJwt = jwtTokenProvider.generateToken(userDetails.getUsername(),
                                userDetails.getId(),
                                userDetails.getEmail());

                        log.debug("token {}", applicationJwt);

                        ResponseCookie cookie;

                        if (prodCookie)
                        {
                            cookie = ResponseCookie.from("jwt", applicationJwt)
                                    .httpOnly(false)
                                    .secure(true)   // Ensure HTTPS enable this for production
                                    .path("/")
                                    .domain("dkkom.com")
                                    .sameSite("None")
                                    .maxAge(Duration.ofMinutes(1))
                                    .build();
                        }
                        else {
                            cookie = ResponseCookie.from("jwt", applicationJwt)
                                    .httpOnly(false)
                                    .secure(false)
                                    .path("/")
                                    .maxAge(Duration.ofMinutes(1))
                                    .build();
                        }

                        exchange.getResponse().addCookie(cookie);

                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getResponse().getHeaders().setLocation(URI.create(callbackUrl));

                        log.debug("callbackUrl {}", callbackUrl);
                        log.debug("response headers {}", exchange.getResponse().getHeaders());
                        log.debug("cookies {}", exchange.getResponse().getCookies());

                        return exchange.getResponse().setComplete();
                    })
                    .onErrorResume(error -> {
                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        return exchange.getResponse().setComplete();
                    });
        };
    }
}
