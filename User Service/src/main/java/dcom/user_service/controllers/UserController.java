package dcom.user_service.controllers;

import dcom.user_service.business.register_user.usecase.RegisterUserUseCase;
import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(registerUserUseCase.registerUser(createUserRequest));
    }
}
