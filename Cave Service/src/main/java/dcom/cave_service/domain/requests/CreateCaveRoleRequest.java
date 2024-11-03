package dcom.cave_service.domain.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCaveRoleRequest {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    private UUID caveId;

    private int permissions;
}
