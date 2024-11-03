package dcom.websocketgateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    @Override
    public void onMessage(Message redisMessage, byte[] pattern) {
        try {
            // Convert Redis message to our custom Message class
            String messageJson = new String(redisMessage.getBody());
            MessageDTO message = objectMapper.readValue(messageJson, MessageDTO.class);

            // Retrieve session and send message
            String channelId = message.getChannelId().toString();
            Set<WebSocketSession> sessions = sessionService.getSessionsForChannel(message.getChannelId().toString());
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message.getContent()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception, possibly logging it for debugging
        }
    }
}
