package dcom.websocketgateway.domain;

import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentCaveInfo {
    private String caveId;
    private List<String> userRoleIds;
    private List<String> channelsWithOverriddenPermissions;
    private UserRolesAndPermissionsCache userRolesAndPermissionsCache;
}
