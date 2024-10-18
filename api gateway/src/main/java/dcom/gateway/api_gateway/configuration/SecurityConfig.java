package dcom.gateway.api_gateway.configuration;

import dcom.gateway.api_gateway.RsaKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.security.interfaces.RSAPublicKey;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final RsaKeyProvider rsaKeyProvider;

    private final SuccessOauthFilter successOauthFilter;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for stateless services
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Prevent security context from being stored on the server
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyExchange().authenticated() // Allow all other exchanges without authentication
                )
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec
                        .authenticationSuccessHandler(successOauthFilter.customAuthenticationSuccessHandler()) // Use custom success handler
                )
                .oauth2ResourceServer(oAuth2ResourceServerSpec ->
                        oAuth2ResourceServerSpec.jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder())) // JWT-based resource server configuration
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Disable HTTP Basic Authentication to avoid session creation
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Disable form login
                .logout(ServerHttpSecurity.LogoutSpec::disable); // Disable logout to avoid session handling

        return http.build();
    }

    @Bean
    public NimbusReactiveJwtDecoder jwtDecoder() {
        RSAPublicKey publicKey = (RSAPublicKey) rsaKeyProvider.getPublicKey();
        return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
    }
}
