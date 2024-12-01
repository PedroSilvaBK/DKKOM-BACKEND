package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.domain.*;
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
public class UpdateChannelListListener {
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    private final static String UPDATE_CHANNEL_LIST_TOPIC = "update-channel-list";

    @KafkaListener(topics = UPDATE_CHANNEL_LIST_TOPIC, groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            CreateChannelResponse createChannelResponse = objectMapper.readValue(record.value(), CreateChannelResponse.class);

            Set<WebSocketSession> sessions = sessionService.getSessionsByCave(createChannelResponse.getCaveId().toString());

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                            Response.<CreateChannelResponse>builder()
                                    .type(EventResponseType.UPDATE_CHANNEL_LIST.toString())
                                    .data(createChannelResponse)
                                    .build()
                    )));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
