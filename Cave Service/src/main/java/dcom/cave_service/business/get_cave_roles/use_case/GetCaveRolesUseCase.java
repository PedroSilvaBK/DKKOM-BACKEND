package dcom.cave_service.business.get_cave_roles.use_case;

import dcom.cave_service.domain.responses.GetCaveRolesResponse;

import java.util.UUID;

public interface GetCaveRolesUseCase {
    GetCaveRolesResponse getCaveRoles(UUID caveId);
}
