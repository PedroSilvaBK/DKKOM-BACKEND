package dcom.cave_service.configuration.security;

import dcom.cave_service.configuration.filter.TokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenFilter tokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure HTTP security for your application
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
////                    authorizationManagerRequestMatcherRegistry.requestMatchers("/cave/**").permitAll();
//                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
//                });
//                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class); // Register JWT filter

        return http.build();
    }
}
