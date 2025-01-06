package dcom.websocketgateway.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserJoinedVoiceChannelEvent {
    @JsonProperty("room_id")
    private String roomId;
    @JsonProperty("user_id")
    private String userId;
    private String username;
    private String caveId;
}
