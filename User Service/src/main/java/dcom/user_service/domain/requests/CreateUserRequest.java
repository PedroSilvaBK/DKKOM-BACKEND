package dcom.user_service.domain.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserRequest {
    @NotNull
    private String username;
    @NotNull
    private String name;
    @NotNull
    private String email;
}
