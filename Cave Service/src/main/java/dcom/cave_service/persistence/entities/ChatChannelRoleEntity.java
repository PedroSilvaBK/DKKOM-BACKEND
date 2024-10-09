package dcom.cave_service.persistence.entities;

import dcom.cave_service.domain.ChatChannelPermissions;
import dcom.cave_service.domain.PermissionType;
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
public class ChatChannelRoleEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChatChannelEntity chatChannelEntity;

    @Column(name = "entity_id")
    private UUID entityId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "entity_type")
    private PermissionType entityType;

    @ElementCollection(targetClass = ChatChannelPermissions.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "channel_role_permissions", joinColumns = @JoinColumn(name = "channel_role_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "permission")
    private Set<ChatChannelPermissions> permissions;
}
