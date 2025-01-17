package dcom.messaging_service.business.create_message;

import dcom.messaging_service.business.CannotSendMessageException;
import dcom.messaging_service.business.clients.permissions_service_client.PermissionServiceClient;
import dcom.messaging_service.business.publishers.MessagePublisher;
import dcom.messaging_service.config.InstantGenerator;
import dcom.messaging_service.config.UUIDGenerator;
import dcom.messaging_service.domain.AuthorDTO;
import dcom.messaging_service.domain.MessageDTO;
import dcom.messaging_service.domain.requests.CreateMessageRequest;
import dcom.messaging_service.persistence.entities.AuthorEntity;
import dcom.messaging_service.persistence.entities.MessageEntity;
import dcom.messaging_service.persistence.repositories.AuthorRepository;
import dcom.messaging_service.persistence.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateMessageUseCaseImpl implements CreateMessageUseCase {
    private final PermissionServiceClient permissionServiceClient;
    private final MessageRepository repository;
    private final AuthorRepository authorRepository;
    private final MessagePublisher publisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UUIDGenerator uuidGenerator;
    private final InstantGenerator instantGenerator;


    public boolean sendMessage(String username, String channelId, String userId, CreateMessageRequest message) {
        boolean canSendMessage = permissionServiceClient.canSendMessage(userId, channelId);

        if (!canSendMessage) {
            throw new CannotSendMessageException("You cannot send messages");
        }

        UUID authorId = UUID.fromString(userId);
        AuthorEntity author = AuthorEntity.builder()
                .id(authorId)
                .username(username)
                .build();

        MessageEntity messageEntity = MessageEntity.builder()
                .id(uuidGenerator.generateUUID())
                .authorId(authorId)
                .attachments(message.getAttachments())
                .channelId(UUID.fromString(message.getChannelId()))
                .timestamp(instantGenerator.getInstantEpochMilli())
                .content(message.getContent())
                .build();

        authorRepository.save(author);
        MessageEntity savedMessage = repository.save(messageEntity);

        publisher.publishToChannel(MessageDTO.builder()
                .id(savedMessage.getId())
                .author(
                        AuthorDTO.builder()
                                .id(savedMessage.getAuthorId())
                                .username(username)
                                .build()
                )
                .channelId(savedMessage.getChannelId())
                .timestamp(savedMessage.getTimestamp())
                .attachments(savedMessage.getAttachments())
                .content(savedMessage.getContent())
                .build());

        kafkaTemplate.send("message-sent", savedMessage.getAttachments());

        return true;
    }
}
