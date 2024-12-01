package dcom.websocketgateway.auth;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final RsaKeyProvider rsaKeyProvider;

    // Generate JWT token using the private key (RSA signing)
    public String generateToken(String username, String id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);
        return Jwts.builder()
                .claims()
                    .add(claims)
                .and()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3000))
                .signWith(rsaKeyProvider.getPrivateKey()) // Use RS512 for RSA signature
                .compact();
    }
}
