package dcom.messaging_service.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.messaging_service.domain.events.UpdateUsername;
import dcom.messaging_service.persistence.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUsernameListener {
    private final ObjectMapper objectMapper;
    private final static String UPDATE_USERNAME_TOPIC = "update-username";
    private final AuthorRepository authorRepository;

    @KafkaListener(topics = UPDATE_USERNAME_TOPIC, groupId = "update-username-message-group")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            UpdateUsername updateUsername = objectMapper.readValue(record.value(), UpdateUsername.class);

            authorRepository.updateAuthorNameByIds(updateUsername.getUsername(), updateUsername.getId());

            log.debug("members username updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
