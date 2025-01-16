//package dcom.messaging_service.business.listeners;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dcom.messaging_service.domain.AuthorDTO;
//import dcom.messaging_service.domain.MessageDTO;
//import dcom.messaging_service.persistence.entities.MessageEntity;
//import dcom.messaging_service.persistence.repositories.MessageRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class UnProcessedMessagesListener {
//    private final ObjectMapper objectMapper;
//    private final MessageRepository messageRepository;
//
//    private final KafkaTemplate<String, MessageDTO> kafkaTemplate;
//
//    @KafkaListener(topics = "unprocessed-messages", groupId = "message-processors")
//    public void onMessage(Message redisMessage) {
//        try {
//            String messageJson = new String(redisMessage.getBody());
//            MessageDTO message = objectMapper.readValue(messageJson, MessageDTO.class);
//
//            MessageEntity messageEntity = MessageEntity.builder()
//                    .id(UUID.randomUUID())
//                    .authorId(message.getAuthor().getId())
//                    .channelId(message.getChannelId())
//                    .timestamp(Instant.now().toEpochMilli())
//                    .content(message.getContent())
//                    .build();
//
//            MessageEntity savedMessage = messageRepository.save(messageEntity);
//
//            log.debug("saved Message: {}", savedMessage);
//
//            kafkaTemplate.send("processed-messages", MessageDTO.builder()
//                    .id(savedMessage.getId())
//                    .author(
//                            AuthorDTO.builder()
//                                    .id(savedMessage.getAuthorId())
//                                    .username(message.getAuthor().getUsername())
//                                    .build()
//                    )
//                    .channelId(savedMessage.getChannelId())
//                    .timestamp(savedMessage.getTimestamp())
//                    .content(savedMessage.getContent())
//                    .build());
//
//            log.debug("message sent to topic");
//
//        } catch (Exception e) {
//            log.error("something went wrong while processing message - {}", e.getMessage());
//        }
//    }
//}
