package dcom.messaging_service.persistence.entities;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("authors")
@Data
@Builder
public class AuthorEntity {

    @PrimaryKey
    private UUID id;

    @NotNull
    @Size(min = 1, max = 50)
    private String username;
}
