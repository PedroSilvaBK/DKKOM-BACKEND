package dcom.messaging_service.business.export_user_messages.usecase;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public interface ExportUserMessagesUseCase {
    File exportMessages(UUID userId, String authUserId) throws IOException;
}
