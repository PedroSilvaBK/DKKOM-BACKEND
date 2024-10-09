package dcom.cave_service.business.delete_voice_channel.use_case_impl;

import dcom.cave_service.business.delete_voice_channel.use_case.DeleteVoiceChannelUseCase;
import dcom.cave_service.persistence.repositories.VoiceChannelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteVoiceChannelUseCaseImpl implements DeleteVoiceChannelUseCase {
    private final VoiceChannelRepository voiceChannelRepository;

    @Transactional
    public boolean deleteChannel(UUID id) {
        if (!voiceChannelRepository.existsById(id)) {
            return false;
        }

        voiceChannelRepository.deleteById(id);

        return true;
    }
}
