package dcom.websocketgateway.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ICECandidateRequest {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("room_id")
    private String roomId;
    @JsonProperty("candidate")
    private Map<String, Object> candidate;
}
