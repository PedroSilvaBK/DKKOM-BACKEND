package dcom.cave_service.persistence.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaveWithChannelInfoDTO {
    private UUID caveId;
    private String caveName;
    private UUID owner;
    private String voiceChannelName;
    private UUID voiceChannelId;
    private String textChannelName;
    private UUID textChannelId;
}
