package dcom.gateway.api_gateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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