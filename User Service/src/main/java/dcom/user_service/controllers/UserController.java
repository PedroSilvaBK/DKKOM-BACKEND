package dcom.user_service.controllers;

import dcom.user_service.business.get_user.usecase.GetUserUseCase;
import dcom.user_service.business.register_user.usecase.RegisterUserUseCase;
import dcom.user_service.business.update_user.use_case.UpdateUserUseCase;
import dcom.user_service.configuration.jwt.JwtTokenProvider;
import dcom.user_service.configuration.jwt.RsaKeyProvider;
import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;


    private final JwtTokenProvider jwtTokenProvider;
    private final RsaKeyProvider rsaKeyProvider;


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserRequest createUserRequest) {
        log.debug("register endpoint reached - {}", createUserRequest);
        return ResponseEntity.ok(registerUserUseCase.registerUser(createUserRequest));
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        log.debug("get user endpoint reached - {}", id);
        return ResponseEntity.ok(getUserUseCase.getUser(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<Boolean> updateUser(@RequestBody @Valid User user) {
        log.debug("update endpoint reached - {}", user);
        return ResponseEntity.ok(updateUserUseCase.updateUser(user));
    }

    @GetMapping("/token")
    public ResponseEntity<String> getNewToken(@RequestHeader("Authorization") String authHeader) {
        String oldToken = authHeader.substring(7);

        Claims claims = decodeJWT(oldToken);
        User user = getUserUseCase.getUser(UUID.fromString(claims.get("id").toString()));

        return ResponseEntity.ok(jwtTokenProvider.generateToken(user.getUsername(), user.getId(), user.getEmail()));
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
