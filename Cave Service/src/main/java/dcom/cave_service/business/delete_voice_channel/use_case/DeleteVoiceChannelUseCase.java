package dcom.cave_service.business.delete_voice_channel.use_case;

import java.util.UUID;

public interface DeleteVoiceChannelUseCase {
    boolean deleteChannel(UUID id);
}
