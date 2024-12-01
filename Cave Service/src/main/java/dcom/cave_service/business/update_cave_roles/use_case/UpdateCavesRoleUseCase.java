package dcom.cave_service.business.update_cave_roles.use_case;

import dcom.cave_service.domain.CaveRole;

import java.util.List;

public interface UpdateCavesRoleUseCase {
    boolean updateCaveRole(List<CaveRole> caveRoles);
}
