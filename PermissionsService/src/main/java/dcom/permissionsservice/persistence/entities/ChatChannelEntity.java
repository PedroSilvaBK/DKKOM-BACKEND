package dcom.permissionsservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "chat_channels")
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
public class ChatChannelEntity extends ChannelEntity {
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "channelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelRoleEntity> chatChannelRoleEntityList;
}
