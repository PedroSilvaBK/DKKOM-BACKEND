package dcom.websocketgateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserJoinedCave {
    private UUID userId;
    private UUID memberId;
    private String username;
    private List<String> channelsUserIsVisible;
}
