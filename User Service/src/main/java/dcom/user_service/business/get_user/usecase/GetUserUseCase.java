package dcom.user_service.business.get_user.usecase;

import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;

import java.util.Optional;

public interface GetUserUseCase {
    Optional<User> getUser(String email);
}
