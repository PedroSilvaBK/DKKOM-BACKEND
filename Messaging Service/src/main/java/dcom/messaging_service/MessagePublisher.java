package dcom.messaging_service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publishToChannel(String channelId, MessageDTO message) {
        redisTemplate.convertAndSend("channel:" + channelId, message);
    }
}
