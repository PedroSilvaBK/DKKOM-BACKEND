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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMessageUseCaseImplTest {
    @Mock
    private PermissionServiceClient permissionServiceClient;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private MessagePublisher messagePublisher;
    @Mock
    private UUIDGenerator uuidGenerator;
    @Mock
    private InstantGenerator instantGenerator;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CreateMessageUseCaseImpl createMessageUseCase;

    @Test
    void sendMessage() {
        String username = "username";
        String channelId = "123e4567-e89b-42d3-a456-556642440000";
        String userId = "123e4563-e89b-42d3-a456-556642440000";
        String messageId = "423e4563-e89b-42d3-a456-556642440000";
        long messageTimeStamp = 1736608230L;

        when(permissionServiceClient.canSendMessage(userId, channelId)).thenReturn(true);

        CreateMessageRequest createMessageRequest = CreateMessageRequest.builder()
                .channelId(channelId)
                .attachments(null)
                .content("message content")
                .build();

        AuthorEntity author = AuthorEntity.builder()
                .id(UUID.fromString(userId))
                .username(username)
                .build();

        MessageEntity messageEntity = MessageEntity.builder()
                .id(UUID.fromString(messageId))
                .authorId(UUID.fromString(userId))
                .attachments(createMessageRequest.getAttachments())
                .channelId(UUID.fromString(channelId))
                .timestamp(messageTimeStamp)
                .content(createMessageRequest.getContent())
                .build();

        MessageDTO messageDTO = MessageDTO.builder()
                .id(UUID.fromString(messageId))
                .author(
                        AuthorDTO.builder()
                                .id(UUID.fromString(userId))
                                .username(username)
                                .build()
                )
                .channelId(UUID.fromString(channelId))
                .timestamp(messageTimeStamp)
                .content("message content")
                .attachments(createMessageRequest.getAttachments())
                .build();

        when(authorRepository.save(author)).thenReturn(author);
        when(messageRepository.save(messageEntity)).thenReturn(messageEntity);
        doNothing().when(messagePublisher).publishToChannel(messageDTO);
        when(kafkaTemplate.send(eq("message-sent"), any())).thenReturn(null);
        when(uuidGenerator.generateUUID()).thenReturn(UUID.fromString(messageId));
        when(instantGenerator.getInstantEpochMilli()).thenReturn(messageTimeStamp);
        boolean sent = createMessageUseCase.sendMessage(username, channelId, userId, createMessageRequest);

        assertTrue(sent);

        verify(kafkaTemplate).send(any(),  any());
        verify(authorRepository).save(author);
        verify(messageRepository).save(messageEntity);
    }

    @Test
    void sendMessage_with_no_permission() {
        String username = "username";
        String channelId = "123e4567-e89b-42d3-a456-556642440000";
        String userId = "123e4563-e89b-42d3-a456-556642440000";

        CreateMessageRequest createMessageRequest = CreateMessageRequest.builder()
                .channelId(channelId)
                .attachments(null)
                .content("message content")
                .build();

        when(permissionServiceClient.canSendMessage(userId, channelId)).thenReturn(false);

        assertThrows(CannotSendMessageException.class, () -> createMessageUseCase.sendMessage(username, channelId, userId, createMessageRequest));

    }
}