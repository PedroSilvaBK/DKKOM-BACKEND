package dcom.userpresenceservice.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetUserStatusImpl {
    private final RedisTemplate<String, Integer> redisTemplate;
    private final static String KEY_PREFIX = "presence:";

    public int setUserStatusAsActive(String userId, int status) {
        String key = KEY_PREFIX + userId;

        if (status == 1) {
            redisTemplate.opsForValue().set(key, 1);
            log.info("User {} marked as active. TTL refreshed to {} seconds", userId, 30);
            return 1;
        }
        else if (status == 2) {
            redisTemplate.opsForValue().set(key, 1, 40, TimeUnit.SECONDS);
            return 2;
        }

        return -1;
    }
}
