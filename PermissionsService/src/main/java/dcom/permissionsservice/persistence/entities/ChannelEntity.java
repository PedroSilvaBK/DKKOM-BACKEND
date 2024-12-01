package dcom.permissionsservice.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "channel")
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@Data
@NoArgsConstructor
public class ChannelEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cave_id", nullable = false)
    private CaveEntity caveEntity;

    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
}
