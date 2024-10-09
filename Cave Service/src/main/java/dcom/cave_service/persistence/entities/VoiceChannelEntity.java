package dcom.cave_service.persistence.entities;

import dcom.cave_service.domain.VoiceChannelPermissions;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "voice_channels")
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
public class VoiceChannelEntity extends ChannelEntity {
    @OneToMany(mappedBy = "voiceChannelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoiceChannelRoleEntity> voiceChannelRoleEntities;
}
