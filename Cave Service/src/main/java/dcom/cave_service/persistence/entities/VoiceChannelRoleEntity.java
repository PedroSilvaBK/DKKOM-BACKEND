package dcom.cave_service.persistence.entities;

import dcom.cave_service.domain.PermissionType;
import dcom.cave_service.domain.VoiceChannelPermissions;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "channel_roles")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoiceChannelRoleEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_id", nullable = false)
    private VoiceChannelEntity voiceChannelEntity;

    @Column(name = "entity_id")
    private UUID entityId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "entity_type")
    private PermissionType entityType;

    @ElementCollection(targetClass = VoiceChannelPermissions.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "channel_role_permissions", joinColumns = @JoinColumn(name = "channel_role_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "permission")
    private Set<VoiceChannelPermissions> permissions;
}
