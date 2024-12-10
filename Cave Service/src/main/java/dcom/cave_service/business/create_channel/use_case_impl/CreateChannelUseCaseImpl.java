package dcom.cave_service.business.create_channel.use_case_impl;

import dcom.cave_service.business.create_channel.use_case.CreateChannelUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.ChannelType;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.requests.CreateChannelRequest;
import dcom.cave_service.domain.responses.CreateChannelResponse;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChatChannelEntity;
import dcom.cave_service.persistence.entities.VoiceChannelEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.ChatChannelRepository;
import dcom.cave_service.persistence.repositories.VoiceChannelRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateChannelUseCaseImpl implements CreateChannelUseCase {
    private final ChatChannelRepository chatChannelRepository;
    private final VoiceChannelRepository voiceChannelRepository;
    private final CaveRepository caveRepository;

    private final KafkaTemplate<String, CreateChannelResponse> kafkaTemplate;

    private final JwtUserDetails jwtUserDetails;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final PermissionsUtils permissionsUtils;

    @Transactional
    public CreateChannelResponse createChannel(CreateChannelRequest createChannelRequest) {
        UserRolesAndPermissionsCache requestUserPermissions = rolesAndPermissionsService.getUserMergedPermissions(UUID.fromString(jwtUserDetails.getUserId()), createChannelRequest.getCaveId());
        if (!permissionsUtils.canManageChannel(requestUserPermissions) && !permissionsUtils.isAdmin(requestUserPermissions))
        {
            throw new Unauthorized("User doesnt have permissions to perform this action");
        }

        if (!caveRepository.existsById(createChannelRequest.getCaveId())) {
            throw new CaveNotFoundException("cave not found");
        }
        UUID uuid = UUID.randomUUID();
        ChannelEntity savedChannelEntity;

        CaveEntity cave = CaveEntity.builder()
                .id(createChannelRequest.getCaveId())
                .build();

        if (createChannelRequest.getChannelType().equals(ChannelType.TEXT_CHANNEL)) {
            ChatChannelEntity chatChannelEntity = ChatChannelEntity.builder()
                    .id(uuid)
                    .name(createChannelRequest.getChannelName())
                    .caveEntity(cave)
                    .build();

            savedChannelEntity = chatChannelRepository.save(chatChannelEntity);
        }
        else {
            VoiceChannelEntity voiceChannelEntity = VoiceChannelEntity.builder()
                    .id(uuid)
                    .name(createChannelRequest.getChannelName())
                    .caveEntity(cave)
                    .build();

            savedChannelEntity = voiceChannelRepository.save(voiceChannelEntity);
        }

        CreateChannelResponse createChannelResponse = CreateChannelResponse.builder()
                .id(savedChannelEntity.getId())
                .caveId(cave.getId())
                .name(savedChannelEntity.getName())
                .type(createChannelRequest.getChannelType())
                .build();

        kafkaTemplate.send("update-channel-list", createChannelResponse);

        return createChannelResponse;
    }
}
