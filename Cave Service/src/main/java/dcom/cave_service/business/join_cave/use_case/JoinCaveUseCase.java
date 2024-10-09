package dcom.cave_service.business.join_cave.use_case;

import dcom.cave_service.domain.requests.JoinCaveRequest;

public interface JoinCaveUseCase {
    boolean joinCave(JoinCaveRequest joinCaveRequest);
}
