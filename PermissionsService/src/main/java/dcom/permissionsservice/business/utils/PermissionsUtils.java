package dcom.permissionsservice.business.utils;

import dcom.sharedlibrarydcom.shared.CavePermissions;
import dcom.sharedlibrarydcom.shared.ChannelPermissionsCache;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class PermissionsUtils {
    public boolean checkChannelPermission(int cavePermissions, UUID channelId, Map<String, ChannelPermissionsCache> channelPermissionsCache,
                                          int cavePermission, int channelPermission){
        // Check permission at cave level
        boolean isCaveAllowed = hasPermission(cavePermissions, cavePermission);

        // if there are no channel permission overwrites return cave level permission
        if (channelPermissionsCache.entrySet().isEmpty())
        {
            return isCaveAllowed;
        }

        // get channel overwrite permissions
        ChannelPermissionsCache channelPermissions = channelPermissionsCache.get(channelId.toString());

        // if the selected channel doesn't have overwritten roles return cave level permission
        if (channelPermissions == null)
        {
            return isCaveAllowed;
        }


        int channelPermissionsAllow = channelPermissions.getAllow();
        int channelPermissionsDeny = channelPermissions.getDeny();

        // if the channel has an overwritten permission with the same value as the cave level return the cave level result
        if (channelPermissionsAllow == 0 && channelPermissionsDeny == 0) {
            return isCaveAllowed;
        }

        // check of the channel permission overwrite
        boolean isAllowed = hasPermission(channelPermissionsAllow, channelPermission);
        boolean isDenied = hasPermission(channelPermissionsDeny, channelPermission);

        if (isAllowed) {
            return true;
        } else if (isDenied) {
            return false;
        } else {
            return isCaveAllowed;
        }
    }

    private boolean hasPermission(int permissions, int permission) {
        // if the use is owner or admin he can do anything
        if ((permissions & CavePermissions.OWNER) == CavePermissions.OWNER || (permissions & CavePermissions.ADMIN) == CavePermissions.ADMIN)
        {
            return true;
        }
        return (permissions & permission) == permission;
    }
}
