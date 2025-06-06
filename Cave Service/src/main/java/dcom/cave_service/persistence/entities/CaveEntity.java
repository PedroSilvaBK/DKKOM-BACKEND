package dcom.cave_service.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "caves")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CaveEntity {
    @Id
    private UUID id;

    @Column(name = "owner", nullable = false)
    private UUID owner;

    @Column(name = "name", nullable = false)
    @Size(max = 50)
    private String name;
    @OneToMany(mappedBy = "caveEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CaveRoleEntity> caveRoleEntities;

    @OneToMany(mappedBy = "caveEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MemberEntity> memberEntities;

    @OneToMany(mappedBy = "caveEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VoiceChannelEntity> voiceChannelEntities;

    @OneToMany(mappedBy = "caveEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatChannelEntity> chatChannelEntities;

    @OneToMany(mappedBy = "caveEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CaveInviteEntity> caveInviteEntities;
}
