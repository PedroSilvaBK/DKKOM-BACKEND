package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.MessageDTO;
import dcom.websocketgateway.domain.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "processed-messages", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            MessageDTO message = objectMapper.readValue(record.value(), MessageDTO.class);


            String channelId = message.getChannelId().toString();

            log.debug("Message received: {} for channel - {}", message, channelId);

            Set<WebSocketSession> sessions = sessionService.getSessionsForChannel(channelId);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                            Response.<MessageDTO>builder()
                                    .type(EventResponseType.CHAT_MESSAGE.toString())
                                    .data(message)
                                    .build()
                    )));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
