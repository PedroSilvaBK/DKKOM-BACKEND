package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerCandidate {
    @JsonProperty("candidate")
    private Candidate candidate;
    @JsonProperty("room_id")
    private String roomId;
    @JsonProperty("user_id")
    private String userId;
}
