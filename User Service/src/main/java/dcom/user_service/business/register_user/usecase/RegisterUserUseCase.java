package dcom.user_service.business.register_user.usecase;

import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;

public interface RegisterUserUseCase {
    User registerUser(CreateUserRequest userRequest);
}
