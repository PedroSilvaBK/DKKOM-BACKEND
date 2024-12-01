package dcom.messaging_service.business.get_messages.use_case;

import dcom.messaging_service.domain.requests.GetMessagesRequest;
import dcom.messaging_service.domain.responses.GetMessagesResponse;

public interface GetMessagesUseCase {
    GetMessagesResponse getMessages(GetMessagesRequest getMessagesRequest);
}
