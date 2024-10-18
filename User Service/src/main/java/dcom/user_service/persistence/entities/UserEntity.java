package dcom.user_service.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {
    @Id
    private UUID id;
    @Size(min = 2, max = 100)
    @Column(unique = true, nullable = false, name = "username")
    private String username;
    @Size(min = 2, max = 100)
    @Column(unique = true, nullable = false, name = "email")
    private String email;
    @Size(min = 2, max = 100)
    @Column(unique = true, nullable = false, name = "name")
    private String name;
}
