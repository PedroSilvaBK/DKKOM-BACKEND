package dcom.cave_service.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCaveInviteRequest {
    private UUID caveId;
    @NotNull
    private LocalDateTime expirationDate;
    @NotNull
    private int maxUsers;
}
