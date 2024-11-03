package dcom.cave_service.business.create_channel_role.use_case_impl;

import dcom.cave_service.business.create_channel_role.use_case.CreateChannelRoleUseCase;
import dcom.cave_service.domain.requests.CreateChannelRoleRequest;
import dcom.cave_service.domain.responses.CreateChannelRoleResponse;
import dcom.cave_service.exceptions.ChannelDoesntExistException;
import dcom.cave_service.exceptions.EntityAlreadyExistsException;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.ChannelRepository;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateChannelRoleUseCaseImpl implements CreateChannelRoleUseCase {
    private final ChannelRoleRepository channelRoleRepository;
    private final ChannelRepository channelRepository;
    private final ModelMapper modelMapper;

    public CreateChannelRoleResponse createChannelRole(CreateChannelRoleRequest createChannelRoleRequest) {
        if (channelDoesntExist(createChannelRoleRequest.getChannelId())) {
            throw new ChannelDoesntExistException("Channel Doesnt exist");
        }

        if (entityAlreadyExists(createChannelRoleRequest.getEntityId(), createChannelRoleRequest.getChannelId())) {
            throw new EntityAlreadyExistsException("Entity Already Exists");
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

        channelRoleRepository.save(channelRoleEntity);

        return modelMapper.map(channelRoleEntity, CreateChannelRoleResponse.class);
    }

    private boolean channelDoesntExist(UUID channelId) {
        return !channelRepository.existsById(channelId);
    }

    private boolean entityAlreadyExists(UUID entityId, UUID channelId) {
        return channelRoleRepository.existsByEntityIdAndChannelEntity_Id(entityId, channelId);
    }
}
