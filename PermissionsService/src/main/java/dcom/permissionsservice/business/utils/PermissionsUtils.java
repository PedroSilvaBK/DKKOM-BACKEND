package dcom.permissionsservice.business.utils;

import dcom.sharedlibrarydcom.shared.CavePermissions;
import dcom.sharedlibrarydcom.shared.ChannelPermissionsCache;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class PermissionsUtils {
    public boolean checkChannelPermission(int cavePermissions, UUID channelId, Map<String, ChannelPermissionsCache> channelPermissionsCache, int cavePermission, int channelPermission){
        boolean isCaveAllowed = hasPermission(cavePermissions, cavePermission);
        if (channelPermissionsCache.entrySet().isEmpty())
        {
            return isCaveAllowed;
        }

        ChannelPermissionsCache channelPermissions = channelPermissionsCache.get(channelId.toString());

        if (channelPermissions == null)
        {
            return isCaveAllowed;
        }


        int channelPermissionsAllow = channelPermissions.getAllow();
        int channelPermissionsDeny = channelPermissions.getDeny();
        if (channelPermissionsAllow == 0 && channelPermissionsDeny == 0) {
            return isCaveAllowed;
        }

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
        if ((permissions & CavePermissions.OWNER) == CavePermissions.OWNER || (permissions & CavePermissions.ADMIN) == CavePermissions.ADMIN)
        {
            return true;
        }
        return (permissions & permission) == permission;
    }
}
