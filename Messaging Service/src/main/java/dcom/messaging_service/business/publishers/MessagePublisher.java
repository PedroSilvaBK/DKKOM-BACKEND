package dcom.messaging_service.business.publishers;

import dcom.messaging_service.domain.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishToChannel(MessageDTO message) {
        kafkaTemplate.send("processed-messages", message);
        log.info("Message published to channel");
        log.debug("messages published: {}", message);
    }
}
