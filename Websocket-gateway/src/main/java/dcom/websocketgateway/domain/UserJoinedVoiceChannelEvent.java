package dcom.websocketgateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserJoinedVoiceChannelEvent {
    private String roomId;
    private String userId;
    private String username;
    private String caveId;
}
