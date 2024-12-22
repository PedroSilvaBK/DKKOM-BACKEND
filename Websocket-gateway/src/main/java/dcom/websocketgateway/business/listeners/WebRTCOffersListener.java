package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.business.VoiceService;
import dcom.websocketgateway.domain.EventResponseType;
import dcom.websocketgateway.domain.SessionDescription;
import dcom.websocketgateway.domain.Response;
import dcom.websocketgateway.domain.WebRTCServerOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebRTCOffersListener {
    private final ObjectMapper objectMapper;
    private final SessionService sessionService;
    private final VoiceService voiceService;

    @KafkaListener(topics = "webrtc-offers", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            WebRTCServerOffer serverOffer = objectMapper.readValue(record.value(), WebRTCServerOffer.class);
            String sessionId = voiceService.getUserSession(serverOffer.getRoomId(), serverOffer.getUserId());

            if (sessionId == null) {
                return;
            }

            WebSocketSession session = sessionService.getSession(sessionId);

            if (session.isOpen())
            {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        Response.<SessionDescription>builder()
                                .type(EventResponseType.WEB_RTC_OFFER.toString())
                                .data(serverOffer.getSessionDescription())
                                .build()
                )));
            }

        } catch (Exception e) {
            log.error("Error processing message record - {} | error - {}",record.value(),e.getMessage(), e);
        }
    }

}
