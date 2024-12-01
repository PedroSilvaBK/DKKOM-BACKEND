package dcom.websocketgateway.controllers;

import dcom.websocketgateway.auth.JwtTokenProvider;
import dcom.websocketgateway.auth.RsaKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final RsaKeyProvider rsaKeyProvider;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping
    public ResponseEntity<String> getOneTimeUseAuthToken(@RequestHeader("Authorization") String authToken) {
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        Claims claims = Jwts.parser()
                .verifyWith(rsaKeyProvider.getPublicKey())
                .build()
                .parseSignedClaims(authToken)
                .getPayload();

        log.debug("websocket authentication for - {}", claims.getSubject());

        return ResponseEntity.ok(
                jwtTokenProvider.generateToken(
                        claims.get("username", String.class),
                        claims.get("id", String.class)
                )
        );
    }
}
