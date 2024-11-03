package dcom.messaging_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {
            String messageJson = new String(redisMessage.getBody());
            MessageDTO message = objectMapper.readValue(messageJson, MessageDTO.class);

            MessageEntity messageEntity = MessageEntity.builder()
                    .id(UUID.randomUUID())
                    .authorId(message.getAuthor().getId())
                    .channelId(message.getChannelId())
                    .timestamp(Instant.now().toEpochMilli())
                    .content(message.getContent())
                    .build();


            MessageEntity savedMessage = messageRepository.save(messageEntity);

            redisTemplate.convertAndSend("channel:" + savedMessage.getChannelId(), MessageDTO.builder()
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

        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception, possibly logging it for debugging
        }
    }
}
