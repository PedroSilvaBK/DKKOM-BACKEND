package dcom.cave_service.business.real_time_events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.cave_service.domain.User;
import dcom.cave_service.domain.events.UserJoinedVoiceChannelEvent;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserJoinedVoiceChannelListener {
    private final ObjectMapper objectMapper;
    private final CaveRepository caveRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, List<User>> redisTemplate;

    @KafkaListener(topics = "user-joined-voice-channel-preprocess", groupId = "cave-service-user-joined-channel")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            UserJoinedVoiceChannelEvent userJoinedVoiceChannel = objectMapper.readValue(record.value(), UserJoinedVoiceChannelEvent.class);

            List<User> usersInChannel = redisTemplate.opsForValue().get(userJoinedVoiceChannel.getRoomId());
            String username = caveRepository.findUsernameByChannelIdAndUserId(UUID.fromString(userJoinedVoiceChannel.getRoomId()), UUID.fromString(userJoinedVoiceChannel.getUserId()));

            if (usersInChannel == null || usersInChannel.isEmpty()) {

                usersInChannel = new ArrayList<>(3);
                usersInChannel.add(
                        User.builder()
                                .id(UUID.fromString(userJoinedVoiceChannel.getUserId()))
                                .username(username)
                        .build()
                );
            }
            else {
                usersInChannel.add(
                        User.builder()
                                .id(UUID.fromString(userJoinedVoiceChannel.getUserId()))
                                .username(username)
                                .build()
                );
            }
            redisTemplate.opsForValue().set(userJoinedVoiceChannel.getRoomId(), usersInChannel);

            userJoinedVoiceChannel.setUsername(username);

            Message<UserJoinedVoiceChannelEvent> message = MessageBuilder
                    .withPayload(userJoinedVoiceChannel)
                    .setHeader(KafkaHeaders.TOPIC, "user-voice-channel-event")
                    .setHeader("event-type", "join")
                    .build();
            kafkaTemplate.send(message);

        } catch (Exception e) {
            log.error("Error processing message record - {} | error - {}",record.value(),e.getMessage(), e);
        }
    }
}
