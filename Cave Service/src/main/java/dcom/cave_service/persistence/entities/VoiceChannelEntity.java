package dcom.cave_service.persistence.entities;

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
    @OneToMany(mappedBy = "channelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelRoleEntity> voiceChannelRoleEntities;
}
