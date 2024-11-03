package dcom.cave_service.domain.requests;

import dcom.cave_service.domain.PermissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateChannelRoleRequest {
    @NotNull
    private UUID channelId;
    @NotNull
    private UUID entityId;
    @NotNull
    private PermissionType type;
    @NotNull
    private String entityName;
    @Min(0)
    @NotNull
    private int allow;

    @Min(0)
    @NotNull
    private int deny;
}
