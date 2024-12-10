package dcom.user_service.configuration.jwt;

import dcom.user_service.domain.JwtUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class RequestAuthenticatedUser {
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public JwtUserDetails getAuthenticatedUserInRequest() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }

        final Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null;
        }

        final Object details = authentication.getDetails();
        if (!(details instanceof JwtUserDetails)) {
            return null;
        }

        return (JwtUserDetails) details;
    }
}
