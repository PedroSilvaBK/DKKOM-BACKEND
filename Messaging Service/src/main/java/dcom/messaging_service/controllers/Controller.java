package dcom.messaging_service.controllers;

import dcom.messaging_service.business.clients.permissions_service_client.PermissionServiceClient;
import dcom.messaging_service.business.get_messages.use_case.GetMessagesUseCase;
import dcom.messaging_service.business.publishers.MessagePublisher;
import dcom.messaging_service.domain.AuthorDTO;
import dcom.messaging_service.domain.MessageDTO;
import dcom.messaging_service.domain.requests.CreateMessageRequest;
import dcom.messaging_service.domain.requests.GetMessagesRequest;
import dcom.messaging_service.domain.responses.GetMessagesResponse;
import dcom.messaging_service.persistence.entities.AuthorEntity;
import dcom.messaging_service.persistence.entities.MessageEntity;
import dcom.messaging_service.persistence.repositories.AuthorRepository;
import dcom.messaging_service.persistence.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class Controller {
    private final MessagePublisher publisher;
    private final MessageRepository repository;
    private final AuthorRepository authorRepository;

    private final GetMessagesUseCase getMessagesUseCase;

    private final PermissionServiceClient permissionServiceClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/{channelId}")
    public ResponseEntity<GetMessagesResponse> getMessages(@PathVariable String channelId, @RequestHeader("X-User-Id") String userId, @RequestParam("pageState")String pageState) {
        return ResponseEntity.ok(
                getMessagesUseCase.getMessages(
                        GetMessagesRequest.builder()
                                .channelId(channelId)
                                .pagingState(pageState)
                                .userId(userId)
                                .build()
                )
        );
    }

    @PostMapping("/{channelId}")
    @Transactional
    public ResponseEntity<Void> sendMessage(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-Username") String username, @PathVariable String channelId, @RequestBody CreateMessageRequest message) {
        boolean canSendMessage = permissionServiceClient.canSendMessage(userId, channelId);

        if (!canSendMessage) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        UUID authorId = UUID.fromString(userId);
        AuthorEntity author = AuthorEntity.builder()
                .id(authorId)
                .username(username)
                .build();

        MessageEntity messageEntity = MessageEntity.builder()
                .id(UUID.randomUUID())
                .authorId(authorId)
                .attachments(message.getAttachments())
                .channelId(UUID.fromString(message.getChannelId()))
                .timestamp(Instant.now().toEpochMilli())
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

        return ResponseEntity.ok().build();
    }
}
