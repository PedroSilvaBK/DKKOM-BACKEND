package dcom.cave_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Member {
    private UUID id;
    private User user;
    private UUID caveId;
    private String nickname;
    private LocalDateTime joinedAt;
    private Set<CaveRole> roles;
}
