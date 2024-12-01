package dcom.cave_service.domain;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRole {
    private UUID id;
    private UUID channelId;
    private UUID entityId;
    private String entityName;
    private PermissionType type;
    private int position;
    private int allow;
    private int deny;
}
