package dcom.websocketgateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.Event;
import dcom.websocketgateway.MessageDTO;
import dcom.websocketgateway.MessagePublisher;
import dcom.websocketgateway.SessionService;
import dcom.websocketsgateway.Author;
import dcom.websocketsgateway.MessageRequest;
import dcom.websocketsgateway.MessageServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class CaveWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();


    private final SessionService sessionService;
    private final MessagePublisher messagePublisher;

    @GrpcClient("message-service")
    private MessageServiceGrpc.MessageServiceBlockingStub messageServiceStub;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionService.saveSession("5cf5294b-dd88-4de2-a6d1-c818287cff09", session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionService.deleteSession("5cf5294b-dd88-4de2-a6d1-c818287cff09");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Event event = objectMapper.readValue(message.getPayload(), Event.class);

        switch (event.getType())
        {
            case "subscribe_channel":
                sessionService.subscribeToChannel((String) event.getProperties().get("channelId"), session);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + event.getType());
        }








        ///////////////////////////////
//        MessageDTO messageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);
//        System.out.println(message.getPayload());

        // Create gRPC request
//        MessageRequest request = MessageRequest.newBuilder()
//                .setAuthor(
//                        Author.newBuilder()
//                                .setId(messageDTO.getAuthor().getId().toString())
//                                .setUsername(messageDTO.getAuthor().getUsername())
//                                .build()
//                )
//                .setChannelId(messageDTO.getChannelId().toString())
//                .setContent(messageDTO.getContent())
//                .build();

//        MessageResponse response = messageServiceStub.processMessage(request);

//        System.out.println(response);

//            messagePublisher.publishToChannel(messageDTO.getChannelId().toString(), messageDTO);
//        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageDTO)));
//
//        try {
//            // Call message service
//            MessageResponse response = messageServiceStub.processMessage(request);
//
//            if (response.getSuccess()) {
//                // Broadcast to all clients in the same channel
//                broadcastMessage(messageDTO.getChannelId(), WebSocketResponse.builder()
//                        .type("MESSAGE")
//                        .messageId(response.getMessageId())
//                        .channelId(messageDTO.getChannelId())
//                        .userId(messageDTO.getUserId())
//                        .content(messageDTO.getContent())
//                        .timestamp(Instant.now())
//                        .build());
//            } else {
//                // Send error to sender
//                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
//                        WebSocketResponse.builder()
//                                .type("ERROR")
//                                .error(response.getError())
//                                .build()
//                )));
//            }
//        } catch (Exception e) {
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
//                    WebSocketResponse.builder()
//                            .type("ERROR")
//                            .error("Failed to process message")
//                            .build()
//            )));
//        }
    }

//    private void broadcastMessage(String channelId, WebSocketResponse response) {
//        String payload = objectMapper.writeValueAsString(response);
//        TextMessage message = new TextMessage(payload);
//
//        sessions.forEach(session -> {
//            try {
//                session.sendMessage(message);
//            } catch (IOException e) {
//                log.error("Failed to send message to session", e);
//            }
//        });
//    }
}
