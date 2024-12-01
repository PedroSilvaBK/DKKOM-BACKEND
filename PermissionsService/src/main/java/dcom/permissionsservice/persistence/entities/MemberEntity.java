package dcom.permissionsservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "members")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cave_id", nullable = false)
    private CaveEntity caveEntity;

    @NotNull
    @Column(name = "user_id")
    private UUID userId;

    @Size(min = 2, max = 50)
    @Column(name = "nickname")
    private String nickname;

    @Size(min = 2, max = 50)
    @NotNull
    @Column(name = "username")
    private String username;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @ManyToMany
    @JoinTable(
            name = "members_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<CaveRoleEntity> roleEntities;
}
