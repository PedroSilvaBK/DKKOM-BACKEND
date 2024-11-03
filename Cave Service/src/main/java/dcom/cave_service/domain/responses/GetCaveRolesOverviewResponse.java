package dcom.cave_service.domain.responses;

import dcom.cave_service.domain.CaveRoleOverview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCaveRolesOverviewResponse {
    private List<CaveRoleOverview> caveRoles;
}
