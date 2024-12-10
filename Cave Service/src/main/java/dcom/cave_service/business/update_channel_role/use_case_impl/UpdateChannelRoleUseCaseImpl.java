package dcom.cave_service.business.update_channel_role.use_case_impl;

import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.update_channel_role.use_case.UpdateChannelRoleUseCase;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.ChannelRole;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.RoleUpdate;
import dcom.cave_service.exceptions.ChannelDoesntExistException;
import dcom.cave_service.exceptions.ChannelRoleDoesntExistException;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.ChannelRepository;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateChannelRoleUseCaseImpl implements UpdateChannelRoleUseCase {
    private final ChannelRoleRepository channelRoleRepository;
    private final ChannelRepository channelRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JwtUserDetails jwtUserDetails;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final PermissionsUtils permissionsUtils;

    @Transactional
    public boolean updateChannelRole(ChannelRole channelRole) {
        if (channelDoesntExist(channelRole.getChannelId())) {
            throw new ChannelDoesntExistException("Channel doesnt exist");
        }

        if (channelRoleAndEntityDontExist(channelRole.getEntityId(), channelRole.getId())) {
            throw new ChannelRoleDoesntExistException("Channel Role doesnt exist");
        }

        UUID caveId = channelRepository.findCaveIdByChannelId(channelRole.getChannelId());
        UserRolesAndPermissionsCache userRolesAndPermissionsCache = rolesAndPermissionsService.getUserMergedPermissions(UUID.fromString(jwtUserDetails.getUserId()), caveId);
        if (!permissionsUtils.canManageChannel(userRolesAndPermissionsCache) && !permissionsUtils.isAdmin(userRolesAndPermissionsCache))
        {
            throw new Unauthorized("User doesnt have permissions to perform this action");
        }

        ChannelRoleEntity mappedEntity = ChannelRoleEntity.builder()
                .entityName(channelRole.getEntityName())
                .id(channelRole.getId())
                .entityId(channelRole.getEntityId())
                .entityType(channelRole.getType())
                .allow(channelRole.getAllow())
                .deny(channelRole.getDeny())
                .channelEntity(ChannelEntity.builder()
                        .id(channelRole.getChannelId())
                        .build())
                .build();

        ChannelRoleEntity savedChannelRole = channelRoleRepository.save(mappedEntity);

        kafkaTemplate.send("preprocess-channel-role-update", RoleUpdate.builder()
                        .channelId(savedChannelRole.getChannelEntity().getId().toString())
                        .caveId(savedChannelRole.getChannelEntity().getCaveEntity().getId().toString())
                        .entityId(savedChannelRole.getEntityId().toString())
                .build());

        return true;
    }

    private boolean channelRoleAndEntityDontExist(UUID entityId, UUID id) {
        return !channelRoleRepository.existsByEntityIdAndId(entityId, id);
    }

    private boolean channelDoesntExist(UUID channelId) {
        return !channelRepository.existsById(channelId);
    }

}
