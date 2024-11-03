package dcom.cave_service.business.update_channel_role.use_case_impl;

import dcom.cave_service.business.update_channel_role.use_case.UpdateChannelRoleUseCase;
import dcom.cave_service.domain.ChannelRole;
import dcom.cave_service.exceptions.ChannelDoesntExistException;
import dcom.cave_service.exceptions.ChannelRoleDoesntExistException;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.ChannelRepository;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateChannelRoleUseCaseImpl implements UpdateChannelRoleUseCase {
    private final ChannelRoleRepository channelRoleRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public boolean updateChannelRole(ChannelRole channelRole) {
        if (channelDoesntExist(channelRole.getChannelId())) {
            throw new ChannelDoesntExistException("Channel doesnt exist");
        }

        if (channelRoleAndEntityDontExist(channelRole.getEntityId(), channelRole.getId())) {
            throw new ChannelRoleDoesntExistException("Channel Role doesnt exist");
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

        channelRoleRepository.save(mappedEntity);

        return true;
    }

    private boolean channelRoleAndEntityDontExist(UUID entityId, UUID id) {
        return !channelRoleRepository.existsByEntityIdAndId(entityId, id);
    }

    private boolean channelDoesntExist(UUID channelId) {
        return !channelRepository.existsById(channelId);
    }

}
