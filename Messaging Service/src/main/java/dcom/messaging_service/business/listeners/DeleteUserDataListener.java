package dcom.messaging_service.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.messaging_service.persistence.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserDataListener {
    private final AuthorRepository authorRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "delete-user-topic", groupId = "delete-user-message-service")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            String userId = objectMapper.readValue(record.value(), String.class);
            UUID uuid = UUID.fromString(userId);

            authorRepository.updateAuthorNameByIds("Deleted User", uuid);
            log.info("Erased message identity for user: {}", userId);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
