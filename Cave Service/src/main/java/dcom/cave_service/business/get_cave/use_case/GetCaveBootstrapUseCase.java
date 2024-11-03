package dcom.cave_service.business.get_cave.use_case;

import dcom.cave_service.domain.CaveBootStrapInformation;

import java.util.UUID;

public interface GetCaveBootstrapUseCase {
    CaveBootStrapInformation getCaveBootstrapUseCave(UUID caveId);
}
