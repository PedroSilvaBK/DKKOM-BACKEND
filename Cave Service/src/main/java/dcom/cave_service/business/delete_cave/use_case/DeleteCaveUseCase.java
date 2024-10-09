package dcom.cave_service.business.delete_cave.use_case;

import java.util.UUID;

public interface DeleteCaveUseCase {
    boolean deleteCave(UUID id);
}
