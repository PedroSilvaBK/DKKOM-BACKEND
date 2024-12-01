package dcom.cave_service.domain;

import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
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
public class CaveBootStrapInformation {
    private UUID caveId;
    private String caveName;
    private UUID owner;
    private List<ChannelOverviewDTO> voiceChannelsOverview;
    private List<ChannelOverviewDTO> textChannelsOverview;
    private UserRolesAndPermissionsCache userPermissionsCache;
}
