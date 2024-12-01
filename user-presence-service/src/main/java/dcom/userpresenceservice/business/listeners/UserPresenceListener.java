package dcom.userpresenceservice.business.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.userpresenceservice.domain.Status;
import dcom.userpresenceservice.domain.UserPresence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPresenceListener {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, UserPresence> kafkaTemplate;

    @KafkaListener(topics = "preprocess-user-status-update", groupId = "user-presence")
    public void onMessage(ConsumerRecord<String, String> record) {
        UserPresence userPresence;

        log.info("Presence Update Event Received");
        try {
            userPresence = objectMapper.readValue(record.value(), UserPresence.class);
            String key = "presence:" + userPresence.getUserId();
            if (userPresence.getStatus().equals(Status.ONLINE))
            {
                redisTemplate.opsForValue().set(key, userPresence);
            }
            else {
                redisTemplate.delete(key);
            }

            log.debug("user presence for {} - {}", userPresence.getUserId(), userPresence.getStatus());

            kafkaTemplate.send("user-status-update", userPresence);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
