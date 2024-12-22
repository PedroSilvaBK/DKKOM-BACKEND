package dcom.cave_service.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoiceChannelOverviewDTO extends ChannelOverviewDTO {
    private List<User> connectedUsers;
}
