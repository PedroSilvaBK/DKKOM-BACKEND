package dcom.cave_service.business.get_channel_roles.use_case_impl;

import dcom.cave_service.business.get_channel_roles.use_case.GetChannelRolesUseCase;
import dcom.cave_service.domain.ChannelRole;
import dcom.cave_service.domain.responses.GetChannelRolesResponse;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetChannelRolesUseCaseImpl implements GetChannelRolesUseCase {
    private final ChannelRoleRepository channelRoleRepository;
    private final ModelMapper modelMapper;

    public GetChannelRolesResponse getChannelRoles(UUID channelId) {
        List<ChannelRole> channelRoles = channelRoleRepository.findAllByChannelEntity_Id(channelId)
                .stream()
                .map(this::map)
                .toList();

        return GetChannelRolesResponse.builder()
                .channelRoles(channelRoles)
                .build();
    }


    private ChannelRole map(ChannelRoleEntity channelRoleEntity) {
        return modelMapper.map(channelRoleEntity, ChannelRole.class);
    }
}
