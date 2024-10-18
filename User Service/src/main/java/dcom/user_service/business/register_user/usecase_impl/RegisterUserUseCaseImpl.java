package dcom.user_service.business.register_user.usecase_impl;

import dcom.user_service.business.create_user.usecase.CreateUserUseCase;
import dcom.user_service.business.get_user.usecase.GetUserUseCase;
import dcom.user_service.business.register_user.usecase.RegisterUserUseCase;
import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public User registerUser(CreateUserRequest userRequest) {
        Optional<User> user = getUserUseCase.getUser(userRequest.getEmail());

        return user.orElseGet(() -> createUserUseCase.createUser(userRequest));
    }
}
