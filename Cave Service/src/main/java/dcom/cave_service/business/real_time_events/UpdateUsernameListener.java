package dcom.cave_service.business.real_time_events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.cave_service.domain.events.UpdateUsername;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUsernameListener {
    private final ObjectMapper objectMapper;
    private final static String UPDATE_USERNAME_TOPIC = "update-username";
    private final MemberRepository memberRepository;

    @KafkaListener(topics = UPDATE_USERNAME_TOPIC, groupId = "update-username-cave-group")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            UpdateUsername updateUsername = objectMapper.readValue(record.value(), UpdateUsername.class);

            List<MemberEntity> members = memberRepository.findAllByUserId(updateUsername.getId());

            for (MemberEntity member : members) {
                member.setUsername(updateUsername.getUsername());
            }

            memberRepository.saveAll(members);
            log.debug("members username updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
