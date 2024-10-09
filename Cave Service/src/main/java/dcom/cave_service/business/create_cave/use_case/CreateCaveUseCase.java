package dcom.cave_service.business.create_cave.use_case;

import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.responses.CreateCaveResponse;

public interface CreateCaveUseCase {
    CreateCaveResponse createCave(CreateCaveRequest request);
}
