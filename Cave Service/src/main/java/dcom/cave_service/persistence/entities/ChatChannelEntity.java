package dcom.cave_service.persistence.entities;

import dcom.cave_service.domain.ChatChannelPermissions;
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
    @OneToMany(mappedBy = "chatChannelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatChannelRoleEntity> chatChannelRoleEntityList;
}
