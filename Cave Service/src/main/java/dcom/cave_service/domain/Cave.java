package dcom.cave_service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cave {
    private UUID id;
    private String name;
    private Set<CaveRole> roles;
    private List<Member> members;
    private List<VoiceChannel> voiceChannels;
    private List<ChatChannel> chatChannels;
}
