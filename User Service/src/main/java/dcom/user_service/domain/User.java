package dcom.user_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String id;
    private String username;
    private String email;
    private String name;
}
