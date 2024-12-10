package dcom.cave_service.business.create_channel_role.use_case_impl;

import dcom.cave_service.business.create_channel_role.use_case.CreateChannelRoleUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.requests.CreateChannelRoleRequest;
import dcom.cave_service.domain.responses.CreateChannelRoleResponse;
import dcom.cave_service.exceptions.ChannelDoesntExistException;
import dcom.cave_service.exceptions.EntityAlreadyExistsException;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.ChannelRepository;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateChannelRoleUseCaseImpl implements CreateChannelRoleUseCase {
    private final ChannelRoleRepository channelRoleRepository;
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RolesAndPermissionsService rolesAndPermissionsService;

    private final JwtUserDetails jwtUserDetails;
    private final PermissionsUtils permissionsUtils;

    public CreateChannelRoleResponse createChannelRole(CreateChannelRoleRequest createChannelRoleRequest) {
        if (channelDoesntExist(createChannelRoleRequest.getChannelId())) {
            throw new ChannelDoesntExistException("Channel Doesnt exist");
        }

        if (entityAlreadyExists(createChannelRoleRequest.getEntityId(), createChannelRoleRequest.getChannelId())) {
            throw new EntityAlreadyExistsException("Entity Already Exists");
        }

        UUID caveId = channelRepository.findCaveIdByChannelId(createChannelRoleRequest.getChannelId());
        UserRolesAndPermissionsCache requestUserPermissions = rolesAndPermissionsService.getUserMergedPermissions(UUID.fromString(jwtUserDetails.getUserId()), caveId);
        if (!permissionsUtils.canManageChannel(requestUserPermissions) && !permissionsUtils.isAdmin(requestUserPermissions))
        {
            throw new Unauthorized("User doesnt have permissions to perform this action");
        }

        ChannelRoleEntity channelRoleEntity = ChannelRoleEntity.builder()
                .id(UUID.randomUUID())
                .entityId(createChannelRoleRequest.getEntityId())
                .channelEntity(ChannelEntity.builder()
                        .id(createChannelRoleRequest.getChannelId())
                        .build())
                .entityType(createChannelRoleRequest.getType())
                .entityName(createChannelRoleRequest.getEntityName())
                .allow(createChannelRoleRequest.getAllow())
                .deny(createChannelRoleRequest.getDeny())
                .build();

        ChannelRoleEntity savedChannelRole = channelRoleRepository.save(channelRoleEntity);

        List<String> userIds = memberRepository.findAllUserIsdByRoleId(channelRoleEntity.getEntityId()).stream().map(UUID::toString).toList();

        Map<String, UserRolesAndPermissionsCache> userRolesAndPermissionsCache = rolesAndPermissionsService.forceUpdate(userIds, savedChannelRole.getChannelEntity().getCaveEntity().getId().toString());

        kafkaTemplate.send("update-user-permissions", userRolesAndPermissionsCache);

        return modelMapper.map(channelRoleEntity, CreateChannelRoleResponse.class);
    }

    private boolean channelDoesntExist(UUID channelId) {
        return !channelRepository.existsById(channelId);
    }

    private boolean entityAlreadyExists(UUID entityId, UUID channelId) {
        return channelRoleRepository.existsByEntityIdAndChannelEntity_Id(entityId, channelId);
    }
}
