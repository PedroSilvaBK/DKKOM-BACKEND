package dcom.cave_service.business.join_cave.use_case;

import dcom.cave_service.domain.responses.JoinCaveResponse;

import java.util.UUID;

public interface JoinCaveUseCase {
    JoinCaveResponse joinCave(UUID inviteId);
}
