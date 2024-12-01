package dcom.user_service.business.register_user.usecase_impl;

import dcom.user_service.business.create_user.usecase.CreateUserUseCase;
import dcom.user_service.business.get_user.usecase.GetUserUseCase;
import dcom.user_service.business.register_user.usecase.RegisterUserUseCase;
import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    @Transactional
    public User registerUser(CreateUserRequest userRequest) {
        Optional<User> user = getUserUseCase.getUser(userRequest.getEmail());

        log.debug("user - {} registered", user);

        return user.orElseGet(() -> createUserUseCase.createUser(userRequest));
    }
}
