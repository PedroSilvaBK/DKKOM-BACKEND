package dcom.cave_service.business.get_cave_members_filtered_by_channel_use_case;

import dcom.cave_service.domain.responses.GetCaveMembersResponse;

import java.util.UUID;

public interface GetCaveMembersFilteredByChannelUseCase {
    GetCaveMembersResponse getCaveMembersResponse(UUID channelId, UUID caveId);
}
