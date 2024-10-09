package dcom.cave_service.domain.requests;

import dcom.cave_service.domain.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateChannelRequest {
    @Size(min = 1, max = 50)
    @NotBlank
    private String channelName;

    @NotNull
    private UUID caveId;

    @NotNull
    private ChannelType channelType;
}
