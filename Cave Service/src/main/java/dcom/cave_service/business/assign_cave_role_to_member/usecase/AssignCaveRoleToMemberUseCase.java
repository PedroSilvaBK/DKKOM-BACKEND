package dcom.cave_service.business.assign_cave_role_to_member.usecase;

import dcom.cave_service.domain.requests.AssignRoleRequest;

import java.util.UUID;

public interface AssignCaveRoleToMemberUseCase {
    boolean assignRole(UUID caveId, AssignRoleRequest assignRoleRequest);
}
