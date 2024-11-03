package dcom.cave_service.domain.requests;

import dcom.cave_service.domain.CaveInviteExpiration;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCaveInviteRequest {
    private UUID caveId;
    @NotNull
    private CaveInviteExpiration caveInviteExpiration;
    @NotNull
    @Max(100)
    private int maxUses;
}
