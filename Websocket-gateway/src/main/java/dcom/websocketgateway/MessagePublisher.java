package dcom.websocketgateway;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publishToChannel(String channelId, MessageDTO message) {
        redisTemplate.convertAndSend("pre-process-channel:" + channelId, message);
    }
}
