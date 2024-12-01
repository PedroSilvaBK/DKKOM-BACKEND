package dcom.user_service.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @NotNull
    private String id;
    @NotNull
    private String username;
    private String email;
    @NotNull
    private String name;
}
