package dcom.cave_service.business.get_caves_by_user_id.use_case;

import java.util.List;
import java.util.UUID;

public interface GetCavesByUserIdUseCase {
    List<String> getCavesIdsByUserId(UUID userId);
}
