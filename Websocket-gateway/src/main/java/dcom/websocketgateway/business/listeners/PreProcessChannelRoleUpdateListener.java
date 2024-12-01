package dcom.websocketgateway.business.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.websocketgateway.business.SessionService;
import dcom.websocketgateway.domain.CurrentCaveInfo;
import dcom.websocketgateway.domain.RoleUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Component
@RequiredArgsConstructor
@Slf4j
public class PreProcessChannelRoleUpdateListener {
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final static String CHANNEL_ROLE_UPDATE_TOPIC_NAME = "preprocess-channel-role-update";
    private final static String CAVE_ROLE_UPDATE_TOPIC_NAME = "preprocess-cave-role-update";

    @KafkaListener(topics = {CHANNEL_ROLE_UPDATE_TOPIC_NAME, CAVE_ROLE_UPDATE_TOPIC_NAME}, groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            String topic = record.topic();

            List<RoleUpdate> updateRoles;
            if (CHANNEL_ROLE_UPDATE_TOPIC_NAME.equals(topic)) {
                updateRoles = new ArrayList<>(1);
                updateRoles.add(
                        objectMapper.readValue(record.value(), RoleUpdate.class)
                );
            }
            else if (CAVE_ROLE_UPDATE_TOPIC_NAME.equals(topic)) {
                updateRoles = objectMapper.readValue(record.value(), new TypeReference<>() {});
            }
            else {
                log.warn("Unknown topic - {}", topic);
                return;
            }

            Set<WebSocketSession> sessions = sessionService.getSessionsByCave(updateRoles.getFirst().getCaveId());

            List<String> usersToUpdate = new ArrayList<>();
            usersToUpdate.add(updateRoles.getFirst().getCaveId());

            for (WebSocketSession session : sessions) {
                CurrentCaveInfo currentCaveInfo = (CurrentCaveInfo) session.getAttributes().get("currentCave");
                List<String> userRoleIds = currentCaveInfo.getUserRoleIds();

                if (CHANNEL_ROLE_UPDATE_TOPIC_NAME.equals(topic)) {
                    if (currentCaveInfo.getChannelsWithOverriddenPermissions().contains(updateRoles.getFirst().getChannelId()))
                    {
                        usersToUpdate.add(session.getAttributes().get("userId").toString());
                    }
                } else {
                    for (RoleUpdate roleUpdate : updateRoles) {
                        if (userRoleIds.contains(roleUpdate.getEntityId()))
                        {
                            usersToUpdate.add(session.getAttributes().get("userId").toString());
                        }
                    }
                }
            }

            kafkaTemplate.send("preprocess-user-permissions-update", usersToUpdate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
