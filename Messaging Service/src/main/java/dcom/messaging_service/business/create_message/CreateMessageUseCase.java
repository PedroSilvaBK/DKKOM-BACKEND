package dcom.messaging_service.business.create_message;

import dcom.messaging_service.domain.requests.CreateMessageRequest;

public interface CreateMessageUseCase {
    boolean sendMessage(String username, String channelId, String userId, CreateMessageRequest message);
}
