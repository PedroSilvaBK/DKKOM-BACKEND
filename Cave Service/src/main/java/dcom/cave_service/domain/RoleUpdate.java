package dcom.cave_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleUpdate {
    private String caveId;
    private String channelId;
    private String entityId;
}
