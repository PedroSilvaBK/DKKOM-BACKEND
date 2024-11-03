package dcom.cave_service.business.get_cave.use_case_impl;

import dcom.cave_service.business.get_cave.use_case.GetCaveBootstrapUseCase;
import dcom.cave_service.domain.CaveBootStrapInformation;
import dcom.cave_service.domain.ChannelOverviewDTO;
import dcom.cave_service.persistence.DTO.CaveWithChannelInfoDTO;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCaveBootstrapUseCaveImpl implements GetCaveBootstrapUseCase {
    private final CaveRepository caveRepository;

    public CaveBootStrapInformation getCaveBootstrapUseCave(UUID caveId) {
        List<CaveWithChannelInfoDTO> caveWithChannelInfoDTOS = caveRepository.findCaveBootstrapInfo(caveId);

        CaveBootStrapInformation caveBootStrapInformation = CaveBootStrapInformation.builder()
                .owner(caveWithChannelInfoDTOS.getFirst().getOwner())
                .caveName(caveWithChannelInfoDTOS.getFirst().getCaveName())
                .caveId(caveWithChannelInfoDTOS.getFirst().getCaveId())
                .textChannelsOverview(new ArrayList<>())
                .voiceChannelsOverview(new ArrayList<>())
                .build();

        caveWithChannelInfoDTOS.forEach(caveWithChannelInfoDTO -> {
            if (!Objects.isNull(caveWithChannelInfoDTO.getTextChannelName()))
            {
                caveBootStrapInformation.getTextChannelsOverview().add(
                        ChannelOverviewDTO.builder()
                                .id(caveWithChannelInfoDTO.getTextChannelId())
                                .name(caveWithChannelInfoDTO.getTextChannelName())
                                .build()
                );

            }

            if (!Objects.isNull(caveWithChannelInfoDTO.getVoiceChannelName()))
            {
                caveBootStrapInformation.getVoiceChannelsOverview().add(
                        ChannelOverviewDTO.builder()
                                .id(caveWithChannelInfoDTO.getVoiceChannelId())
                                .name(caveWithChannelInfoDTO.getVoiceChannelName())
                                .build()
                );

            }
        });

        return caveBootStrapInformation;
    }
}
