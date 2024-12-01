package dcom.cave_service.business.get_cave.use_case_impl;

import dcom.cave_service.business.get_cave.use_case.GetCaveBootstrapUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.domain.CaveBootStrapInformation;
import dcom.cave_service.domain.ChannelOverviewDTO;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.persistence.DTO.CaveOverviewInfo;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCaveBootstrapUseCaveImpl implements GetCaveBootstrapUseCase {
    private final CaveRepository caveRepository;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final JwtUserDetails jwtUserDetails;

    public CaveBootStrapInformation getCaveBootstrapUseCave(UUID caveId, String authUserId) {
        List<CaveOverviewInfo> caveOverviewInfos = caveRepository.findCaveBootstrapInfo(caveId);
        List<ChannelEntity> voiceChannelEntities = caveRepository.findVoiceChannels(caveId);
        List<ChannelEntity> chatChannel = caveRepository.findChatChannels(caveId);

        CaveBootStrapInformation caveBootStrapInformation = CaveBootStrapInformation.builder()
                .owner(caveOverviewInfos.getFirst().getOwner())
                .caveName(caveOverviewInfos.getFirst().getCaveName())
                .caveId(caveOverviewInfos.getFirst().getCaveId())
                .textChannelsOverview(new ArrayList<>())
                .voiceChannelsOverview(new ArrayList<>())
                .build();

        voiceChannelEntities.forEach(voiceChannelEntity -> caveBootStrapInformation.getVoiceChannelsOverview().add(
                ChannelOverviewDTO.builder()
                        .id(voiceChannelEntity.getId())
                        .name(voiceChannelEntity.getName())
                        .build()
        ));

        chatChannel.forEach(chatChannelEntity -> caveBootStrapInformation.getTextChannelsOverview().add(
                ChannelOverviewDTO.builder()
                        .id(chatChannelEntity.getId())
                        .name(chatChannelEntity.getName())
                        .build()
        ));

        UserRolesAndPermissionsCache userPermissionsCache = rolesAndPermissionsService.forceUpdate(UUID.fromString(authUserId), caveId);

        caveBootStrapInformation.setUserPermissionsCache(userPermissionsCache);

        return caveBootStrapInformation;
    }
}
