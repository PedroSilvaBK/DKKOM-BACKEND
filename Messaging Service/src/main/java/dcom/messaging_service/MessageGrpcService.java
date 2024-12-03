//package dcom.messaging_service;
//
//import com.dcom.messaging_service.MessageRequest;
//import com.dcom.messaging_service.MessageResponse;
//import com.dcom.messaging_service.MessageServiceGrpc;
//import dcom.messaging_service.domain.AuthorDTO;
//import dcom.messaging_service.domain.MessageDTO;
//import dcom.messaging_service.persistence.entities.MessageEntity;
//import dcom.messaging_service.persistence.repositories.MessageRepository;
//import io.grpc.stub.StreamObserver;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.time.Instant;
//import java.util.UUID;
//
//public class MessageGrpcService extends MessageServiceGrpc.MessageServiceImplBase {
//    private MessageRepository messageRepository;
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Override
//    public void processMessage(MessageRequest request, StreamObserver<MessageResponse> responseObserver) {
//        MessageEntity messageEntity = MessageEntity.builder()
//                .id(UUID.randomUUID())
//                .authorId(UUID.fromString(request.getAuthor().getId()))
//                .channelId(UUID.fromString(request.getChannelId()))
//                .timestamp(Instant.now().toEpochMilli())
//                .content(request.getContent())
//                .build();
//
//        MessageEntity savedMessage = messageRepository.save(messageEntity);
//
//        redisTemplate.convertAndSend("channel:" + savedMessage.getChannelId(), MessageDTO.builder()
//                        .id(savedMessage.getId())
//                        .author(
//                                AuthorDTO.builder()
//                                        .id(savedMessage.getAuthorId())
//                                        .username(request.getAuthor().getUsername())
//                                        .build()
//                        )
//                        .channelId(savedMessage.getChannelId())
//                        .timestamp(savedMessage.getTimestamp())
//                        .content(savedMessage.getContent())
//                .build());
//
////        MessageResponse response = MessageResponse.newBuilder()
////                .setId(savedMessage.getId().toString())
////                .setAuthorId(savedMessage.getAuthorId().toString())
////                .setChannelId(savedMessage.getChannelId().toString())
////                .setContent(savedMessage.getContent())
////                .setTimestamp(savedMessage.getTimestamp().toEpochSecond(ZoneOffset.UTC))
////                .build();
//
////        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }
//}
