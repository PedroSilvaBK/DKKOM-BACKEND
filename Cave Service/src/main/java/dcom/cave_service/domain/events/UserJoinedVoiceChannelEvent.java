package dcom.cave_service.domain.events;

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
    private String caveId;
    private String username;
}
