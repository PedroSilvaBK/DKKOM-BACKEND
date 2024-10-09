package dcom.cave_service.business.create_cave_role.use_case;

import dcom.cave_service.domain.requests.CreateCaveRoleRequest;
import dcom.cave_service.domain.responses.CreateCaveRoleResponse;

public interface CreateCaveRoleUseCase {
    CreateCaveRoleResponse createCaveRole(CreateCaveRoleRequest createCaveRoleRequest);
}
