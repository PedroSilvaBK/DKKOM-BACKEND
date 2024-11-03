package dcom.cave_service.business.get_cave_roles_overview.use_case;

import dcom.cave_service.domain.responses.GetCaveRolesOverviewResponse;

import java.util.UUID;

public interface GetCaveRolesOverviewUseCase {
    GetCaveRolesOverviewResponse getCaveRoles(UUID caveId);
}
