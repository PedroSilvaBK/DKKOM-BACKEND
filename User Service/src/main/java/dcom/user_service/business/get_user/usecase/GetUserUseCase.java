package dcom.user_service.business.get_user.usecase;

import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;

import java.util.Optional;
import java.util.UUID;

public interface GetUserUseCase {
    Optional<User> getUser(String email);
    User getUser(UUID id);
}
