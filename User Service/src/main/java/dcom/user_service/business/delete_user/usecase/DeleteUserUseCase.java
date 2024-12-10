package dcom.user_service.business.delete_user.usecase;

import java.util.UUID;

public interface DeleteUserUseCase {
    boolean deleteUser(UUID userId);
}
