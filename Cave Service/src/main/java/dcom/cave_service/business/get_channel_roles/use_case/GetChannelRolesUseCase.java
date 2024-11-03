package dcom.cave_service.business.get_channel_roles.use_case;

import dcom.cave_service.domain.responses.GetChannelRolesResponse;

import java.util.UUID;

public interface GetChannelRolesUseCase {
    GetChannelRolesResponse getChannelRoles(UUID channelId);
}
