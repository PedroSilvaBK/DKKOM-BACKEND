package dcom.cave_service.business.create_cave_invite.use_case;

import dcom.cave_service.domain.requests.CreateCaveInviteRequest;
import dcom.cave_service.domain.responses.CreateCaveInviteResponse;

public interface CreateCaveInviteUseCase {
    CreateCaveInviteResponse createCaveInvite(CreateCaveInviteRequest createCaveInviteRequest);
}
