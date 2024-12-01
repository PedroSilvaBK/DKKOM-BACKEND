package dcom.permissionsservice.persistence.entities;

import dcom.permissionsservice.domain.PermissionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "channel_roles")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRoleEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channelEntity;

    @Column(name = "entity_id")
    @NotNull
    private UUID entityId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "entity_type")
    @NotNull
    private PermissionType entityType;

    @Column(name = "entity_name")
    @NotNull
    private String entityName;

    @Column(name = "allow")
    @NotNull
    private int allow;

    @Column(name = "deny")
    @NotNull
    private int deny;
}
