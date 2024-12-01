package dcom.cave_service.business.get_cave_members_filtered_by_channel_use_case;

import dcom.cave_service.business.get_cave_members.use_case.GetCaveMembersUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.MemberOverview;
import dcom.cave_service.domain.responses.GetCaveMembersResponse;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCaveMembersFilteredByChannelUseCaseImpl implements GetCaveMembersFilteredByChannelUseCase {
    private final GetCaveMembersUseCase getCaveMembersUseCase;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final PermissionsUtils permissionsUtils;

    public GetCaveMembersResponse getCaveMembersResponse(UUID channelId, UUID caveId) {
        GetCaveMembersResponse getCaveMembersResponse = getCaveMembersUseCase.getCaveMembers(caveId);

        List<UserRolesAndPermissionsCache> usersPermissions = rolesAndPermissionsService.getUsersMergedPermissions(
            getCaveMembersResponse.getMemberOverviews().stream().map(MemberOverview::getUserId).toList(),
            caveId
        );

        List<MemberOverview> filteredMembers = new ArrayList<>();

        for (int i = 0; i < getCaveMembersResponse.getMemberOverviews().size(); i++) {
            if (permissionsUtils.canSeeChannel(usersPermissions.get(i), channelId.toString()) ||
                    permissionsUtils.isOwner(usersPermissions.get(i)) ||
                    permissionsUtils.isAdmin(usersPermissions.get(i)))
            {
                filteredMembers.add(getCaveMembersResponse.getMemberOverviews().get(i));
            }
        }

        getCaveMembersResponse.setMemberOverviews(filteredMembers);

        return getCaveMembersResponse;
    }
}
