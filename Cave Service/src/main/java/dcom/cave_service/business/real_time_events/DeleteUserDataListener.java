package dcom.cave_service.business.real_time_events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.cave_service.domain.events.UpdateUsername;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserDataListener {
    private final MemberRepository memberRepository;
    private final CaveRepository caveRepository;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "delete-user-topic", groupId = "delete-user-cave-service")
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            String userId = objectMapper.readValue(record.value(), String.class);
            UUID uuid = UUID.fromString(userId);
            // delete all user caves
            int cavesDeleted = caveRepository.deleteAllByOwner(uuid);
            if (cavesDeleted > 0) {
                log.error("Caves for user {} could not be deleted", userId);
            }

            // remove user from all caves he is in
            int deletedFromCaves = memberRepository.deleteAllByUserId(uuid);
            if (deletedFromCaves > 0) {
                log.error("Members for user {} could not be deleted", userId);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
