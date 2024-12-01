package dcom.cave_service.domain.events;

import dcom.cave_service.domain.CaveRoleOverview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleAssignedToMember {
    private UUID caveId;
    private UUID userId;
    private List<String> channelsSideListsWhereUserAppears;
    private List<CaveRoleOverview> caveRoleOverviews;
}
