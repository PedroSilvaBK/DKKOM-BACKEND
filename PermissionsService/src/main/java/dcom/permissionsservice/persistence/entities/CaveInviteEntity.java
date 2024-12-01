package dcom.permissionsservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cave_invites")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaveInviteEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cave_id", nullable = false)
    private CaveEntity caveEntity;

    @NotNull
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Max(100)
    @Min(-1)
    @NotNull
    @Column(name = "max_uses")
    private int maxUses;

    @Max(100)
    @Min(0)
    @NotNull
    @Column(name = "invite_uses")
    private int inviteUses;

    public void increaseInviteUses() {
        this.inviteUses++;
    }
}
