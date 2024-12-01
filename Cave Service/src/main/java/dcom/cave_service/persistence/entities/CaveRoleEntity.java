package dcom.cave_service.persistence.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cave_roles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(exclude = "caveEntity")
public class CaveRoleEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cave_id", nullable = false)
    private CaveEntity caveEntity;

    @Size(min = 1, max = 50)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "permissions", nullable = false)
    private int permissions;
}
