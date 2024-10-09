package dcom.cave_service.domain.requests;

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
public class JoinCaveRequest {
    @NotNull
    private UUID inviteId;
    @NotNull
    private UUID userId;
}
