package dcom.cave_service.business.create_channel_role.use_case;

import dcom.cave_service.domain.requests.CreateChannelRoleRequest;
import dcom.cave_service.domain.responses.CreateChannelRoleResponse;

public interface CreateChannelRoleUseCase {
    CreateChannelRoleResponse createChannelRole(CreateChannelRoleRequest createChannelRoleRequest);
}
