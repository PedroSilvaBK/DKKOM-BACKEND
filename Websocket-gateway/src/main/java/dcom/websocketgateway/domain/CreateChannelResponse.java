package dcom.websocketgateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateChannelResponse {
    private UUID id;
    private UUID caveId;
    private String name;
    private ChannelType type;
}
