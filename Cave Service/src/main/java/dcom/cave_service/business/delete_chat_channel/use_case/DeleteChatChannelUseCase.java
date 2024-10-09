package dcom.cave_service.business.delete_chat_channel.use_case;

import java.util.UUID;

public interface DeleteChatChannelUseCase {
    boolean deleteChannel(UUID id);
}
