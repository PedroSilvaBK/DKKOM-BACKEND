package dcom.cave_service.domain;

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
public class CaveInvite {
    private UUID id;
    private UUID caveId;
    private LocalDateTime expirationDate;
    private int maxUses;
    private int inviteUses;

    public boolean validateInvite(){
        return validateExpirationDate() && validateUses();
    }

    private boolean validateExpirationDate()
    {
        return expirationDate.isAfter(LocalDateTime.now());
    }

    private boolean validateUses()
    {
        return inviteUses <= maxUses;
    }
}
