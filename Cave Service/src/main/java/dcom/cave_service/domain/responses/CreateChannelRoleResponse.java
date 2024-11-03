package dcom.cave_service.domain.responses;

import dcom.cave_service.domain.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateChannelRoleResponse {
    private UUID id;
    private UUID channelId;
    private UUID entityId;
    private PermissionType type;
    private String entityName;
    private int allow;
    private int deny;
}
