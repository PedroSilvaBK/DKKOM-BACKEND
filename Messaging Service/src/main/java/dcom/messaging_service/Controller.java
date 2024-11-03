package dcom.messaging_service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class Controller {
    private final MessagePublisher publisher;
    private final MessageRepository repository;

    @PostMapping("/{channelId}")
    public ResponseEntity<Void> teset(@PathVariable String channelId, @RequestBody MessageDTO message) {

        MessageEntity messageEntity = MessageEntity.builder()
                .id(UUID.randomUUID())
                .authorId(message.getAuthor().getId())
                .channelId(message.getChannelId())
                .timestamp(Instant.now().toEpochMilli())
                .content(message.getContent())
                .build();


        MessageEntity savedMessage = repository.save(messageEntity);

        publisher.publishToChannel("channel:" + savedMessage.getChannelId(), MessageDTO.builder()
                .id(savedMessage.getId())
                .author(
                        AuthorDTO.builder()
                                .id(savedMessage.getAuthorId())
                                .username(message.getAuthor().getUsername())
                                .build()
                )
                .channelId(savedMessage.getChannelId())
                .timestamp(savedMessage.getTimestamp())
                .content(savedMessage.getContent())
                .build());

        return ResponseEntity.ok().build();
    }
}
