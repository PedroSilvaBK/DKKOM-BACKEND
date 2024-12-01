package dcom.websocketgateway.business.user_status;

import dcom.websocketgateway.domain.Status;
import dcom.websocketgateway.domain.UserPresence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatusServiceImpl implements UserStatusService {
    private final KafkaTemplate<String, UserPresence> kafkaTemplate;
    public void updateUserStatus(String userId, Status status) {
        log.debug("Updating user status for user {}", userId);
        kafkaTemplate.send("preprocess-user-status-update",
                UserPresence.builder()
                        .status(status)
                        .userId(userId)
                        .build()
        );
    }
}
