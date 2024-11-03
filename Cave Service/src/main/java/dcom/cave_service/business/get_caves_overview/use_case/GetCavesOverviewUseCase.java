package dcom.cave_service.business.get_caves_overview.use_case;

import dcom.cave_service.domain.responses.GetCavesOverviewResponse;

import java.util.UUID;

public interface GetCavesOverviewUseCase {
    GetCavesOverviewResponse getCavesOverview(UUID userId);
}
