package dcom.cave_service.configuration.filter;

import dcom.cave_service.configuration.RsaKeyProvider;
import dcom.cave_service.domain.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final RsaKeyProvider rsaKeyProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwtToken = null;

        // Extract JWT from the Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }

        if (jwtToken != null) {
            // Validate the token and set the security context
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(rsaKeyProvider.getPublicKey())
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();

                // Create a new JwtUserDetails object
                String userId = claims.get("id", String.class); // Assuming the user ID is in the subject
                String username = claims.get("username", String.class);
                String email = claims.get("email", String.class);

                JwtUserDetails userDetails = JwtUserDetails.builder()
                        .email(email)
                        .username(username)
                        .userId(userId)
                        .build();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userId, null, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                authenticationToken.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (JwtException e) {
                // Token is invalid or expired, handle accordingly
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
