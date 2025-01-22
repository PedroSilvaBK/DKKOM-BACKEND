package dcom.userpresenceservice.business;

import dcom.userpresenceservice.domain.Status;
import dcom.userpresenceservice.domain.UserPresence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class CleanUpOfflineUsersJob {
    private final RedisTemplate<String, Integer> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRate = 10000)
    public void processUserPresence() {
        Set<String> keys = redisTemplate.keys("presence:*");
        if (keys != null && !keys.isEmpty()) {
            List<String> keysToBeRemoved = new ArrayList<>();

            for (String key : keys) {
                String userId = key.substring(9);
                Integer value = redisTemplate.opsForValue().get(key);
                Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
                if (value != null) {
                    if ((ttl == null || ttl <= 15000) && (ttl != null && ttl != -1)) {
                        // If TTL expired, mark the user for deletion
                        keysToBeRemoved.add(key);

                        kafkaTemplate.send("user-status-update",
                                UserPresence.builder()
                                        .userId(userId)
                                        .status(Status.OFFLINE)
                                        .build()
                        );
                        log.info("User {} marked for deletion", userId);
                    }
                }
            }

            // Delete keys marked for deletion
            if (!keysToBeRemoved.isEmpty()) {
                redisTemplate.delete(keysToBeRemoved);
                log.info("Deleted keys: {}", keysToBeRemoved);
            }
        }
    }
}
