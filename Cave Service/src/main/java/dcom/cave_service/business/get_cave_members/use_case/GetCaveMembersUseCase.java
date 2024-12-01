package dcom.cave_service.business.get_cave_members.use_case;

import dcom.cave_service.domain.responses.GetCaveMembersResponse;

import java.util.UUID;

public interface GetCaveMembersUseCase {
    GetCaveMembersResponse getCaveMembers(UUID caveId);
}
