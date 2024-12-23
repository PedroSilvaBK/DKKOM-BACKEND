package dcom.websocketgateway.business.listeners;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Candidate {
    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;
    private String usernameFragment;

}