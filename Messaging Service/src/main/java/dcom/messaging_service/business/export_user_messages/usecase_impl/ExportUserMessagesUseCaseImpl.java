package dcom.messaging_service.business.export_user_messages.usecase_impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.messaging_service.business.Unauthorized;
import dcom.messaging_service.business.export_user_messages.usecase.ExportUserMessagesUseCase;
import dcom.messaging_service.persistence.entities.MessageEntity;
import dcom.messaging_service.persistence.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportUserMessagesUseCaseImpl implements ExportUserMessagesUseCase {
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper; // Injected ObjectMapper for JSON serialization

    public File exportMessages(UUID userId, String authUserId) throws IOException {
        if (!userId.toString().equals(authUserId)) {
            throw new Unauthorized("Wrong user ID");
        }

        // Fetch all messages for the user
        List<MessageEntity> allUserSentMessages = messageRepository.findAllAuthorMessages(userId);

        // Convert messages to JSON and write to a file
        File jsonFile = new File("user_messages_" + userId + ".json");
        objectMapper.writeValue(jsonFile, allUserSentMessages);

        // Return the generated file (can be sent to the requester via email, etc.)
        return jsonFile;
    }
}
