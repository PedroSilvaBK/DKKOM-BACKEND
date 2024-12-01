package dcom.user_service.business.update_user.use_case;

import dcom.user_service.domain.User;

public interface UpdateUserUseCase {
    boolean updateUser(User user);
}
