package dcom.cave_service.business.delete_chat_channel.use_case_impl;

import dcom.cave_service.business.delete_chat_channel.use_case.DeleteChatChannelUseCase;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.ChatChannelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteChatChannelUseCaseImpl implements DeleteChatChannelUseCase {
    private final ChatChannelRepository chatChannelRepository;

    @Transactional
    public boolean deleteChannel(UUID id) {
        if (!chatChannelRepository.existsById(id)) {
            return false;
        }

        chatChannelRepository.deleteById(id);

        return true;
    }
}
