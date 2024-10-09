package dcom.cave_service.domain;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Builder
@Data
@RequiredArgsConstructor
public class VoiceChannelRole {
    private final UUID id;
    private final UUID channelId;
    private final UUID entityId;
    private final PermissionType type;
    private final Set<VoiceChannelPermissions> permissions;
}
