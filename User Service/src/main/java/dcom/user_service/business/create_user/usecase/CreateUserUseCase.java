package dcom.user_service.business.create_user.usecase;

import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;

public interface CreateUserUseCase {
    User createUser(CreateUserRequest createUserRequest);
}
