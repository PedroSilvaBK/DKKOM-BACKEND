package dcom.permissionsservice.business.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.permissionsservice.business.permissions_service.PermissionsService;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserPermissionsListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PermissionsService permissionsService;

    @KafkaListener(topics = "preprocess-user-permissions-update", groupId = "permissions-processors")
    public void onMessage(ConsumerRecord<String, String> record){
        try {
            // the first item is the cave Id
            log.info("Received permission update: {}", record);

            List<String> userIdsToUpdate = objectMapper.readValue(record.value(), new TypeReference<>() {});
            String caveId = userIdsToUpdate.getFirst();
            userIdsToUpdate.removeFirst();

            log.debug("Permission Update for users - {} in cave - {}", userIdsToUpdate, caveId);
            Map<String, UserRolesAndPermissionsCache> updatedUserPermissions = permissionsService.forceUpdate(userIdsToUpdate, caveId);
            if (userIdsToUpdate.size() == 1) {
                log.debug("single user permission update - {}", userIdsToUpdate);
                kafkaTemplate.send("update-user-permissions", updatedUserPermissions);
            }
            else {
                log.debug("Multi user permission update - {}", userIdsToUpdate);
                kafkaTemplate.send("update-users-permissions", updatedUserPermissions);
            }

        }
        catch (Exception e){
            log.error("Something went wrong while processing permission update - {}", e.getMessage());
        }
    }
}
