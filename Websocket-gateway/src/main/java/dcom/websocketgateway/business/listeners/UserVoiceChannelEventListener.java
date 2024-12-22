package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.VoiceService;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.Response;
import dcom.websocketgateway.domain.UserJoinedVoiceChannelEvent;
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
public class UserVoiceChannelEventListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;

    @KafkaListener(topics = "user-voice-channel-event", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            UserJoinedVoiceChannelEvent userJoinedVoiceChannelEvent = objectMapper.readValue(record.value(), UserJoinedVoiceChannelEvent.class);
            String eventType = new String(record.headers().lastHeader("event-type").value());

            Set<WebSocketSession> sessions = sessionService.getSessionsByCave(userJoinedVoiceChannelEvent.getCaveId());
            log.debug("Sessions retrieved for user - {} - {}", userJoinedVoiceChannelEvent.getUserId(), sessions.size());

            for (WebSocketSession session : sessions) {
                if (session.isOpen())
                {
                    String message;
                    if (eventType.equals("join")){
                        message = objectMapper.writeValueAsString(
                                Response.<UserJoinedVoiceChannelEvent>builder()
                                        .type(EventResponseType.USER_JOINED_VOICE_CHANNEL.toString())
                                        .data(userJoinedVoiceChannelEvent)
                                        .build()
                        );
                    }
                    else {
                        message = objectMapper.writeValueAsString(
                                Response.<UserJoinedVoiceChannelEvent>builder()
                                        .type(EventResponseType.USER_DISCONNECT_VOICE_CHANNEL.toString())
                                        .data(userJoinedVoiceChannelEvent)
                                        .build()
                        );
                    }

                    session.sendMessage(new TextMessage(message));
                }
            }

        } catch (Exception e) {
            log.error("Error processing message record - {} | error - {}",record.value(),e.getMessage(), e);
        }
    }
}
