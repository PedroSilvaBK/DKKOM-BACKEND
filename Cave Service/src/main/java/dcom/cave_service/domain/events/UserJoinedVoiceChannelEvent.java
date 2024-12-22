package dcom.cave_service.domain.events;

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
    private String caveId;
    private String username;
}
