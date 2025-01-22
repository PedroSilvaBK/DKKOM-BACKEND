package dcom.userpresenceservice.business.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.userpresenceservice.business.SetUserStatusImpl;
import dcom.userpresenceservice.domain.Status;
import dcom.userpresenceservice.domain.UserPresence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPresenceListener {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SetUserStatusImpl setUserStatus;

    @KafkaListener(topics = "preprocess-user-status-update", groupId = "user-presence")
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info("Presence Update Event Received");
        try {
            String presence = objectMapper.readValue(record.value(), String.class);
            String[] statusAndUserId = presence.split(":");
            int status = setUserStatus.setUserStatusAsActive(statusAndUserId[1], Integer.parseInt(statusAndUserId[0]));

            if (status == 1) {
                kafkaTemplate.send("user-status-update", UserPresence.builder().userId(statusAndUserId[1]).status(Status.ONLINE).build());
                log.debug("User Set as online {}", statusAndUserId[1]);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
