package dcom.user_service.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import dcom.user_service.business.delete_user.usecase.DeleteUserUseCase;
import dcom.user_service.business.get_user.usecase.GetUserUseCase;
import dcom.user_service.business.register_user.usecase.RegisterUserUseCase;
import dcom.user_service.business.update_user.use_case.UpdateUserUseCase;
import dcom.user_service.configuration.jwt.JwtTokenProvider;
import dcom.user_service.configuration.jwt.RsaKeyProvider;
import dcom.user_service.domain.TokenRequest;
import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    private final JwtTokenProvider jwtTokenProvider;
    private final RsaKeyProvider rsaKeyProvider;

    @Value("${google.oauth.client.id}")
    private String CLIENT_ID;

    @PostMapping("/token")
    public ResponseEntity<String> getNewToken(@RequestBody TokenRequest tokenRequest) {
        try {
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenRequest.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                CreateUserRequest createUserRequest = CreateUserRequest.builder()
                        .email(email)
                        .name(name)
                        .build();

                User user = registerUserUseCase.registerUser(createUserRequest);

                return ResponseEntity.ok(jwtTokenProvider.generateToken(user.getUsername(), user.getId(), user.getEmail()));
            } else {
                return ResponseEntity.status(401).build();
            }
        } catch (Exception e) {
            log.error("Could not very google auth token %{}" ,e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable UUID userId) {
        log.debug("delete endpoint reached - {}", userId);
        return ResponseEntity.ok(deleteUserUseCase.deleteUser(userId));
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
