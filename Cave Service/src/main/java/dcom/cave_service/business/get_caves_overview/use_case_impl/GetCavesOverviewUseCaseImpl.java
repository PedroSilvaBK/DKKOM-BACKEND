package dcom.cave_service.business.get_caves_overview.use_case_impl;

import dcom.cave_service.business.get_caves_overview.use_case.GetCavesOverviewUseCase;
import dcom.cave_service.domain.CaveOverview;
import dcom.cave_service.domain.responses.GetCavesOverviewResponse;
import dcom.cave_service.exceptions.IdMismatchException;
import dcom.cave_service.persistence.DTO.CaveOverviewDTO;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCavesOverviewUseCaseImpl implements GetCavesOverviewUseCase {
    private final CaveRepository caveRepository;
    private final ModelMapper modelMapper;

    public GetCavesOverviewResponse getCavesOverview(UUID userId, String authUserId) {
        if (!authUserId.equals(userId.toString())) {
            throw new IdMismatchException("Ids dont match");
        }

        Optional<List<CaveOverviewDTO>> caveOverviews = caveRepository.findAllCavesByUserId(userId);

        return GetCavesOverviewResponse.builder()
                .caveOverviews(
                        caveOverviews.map(caves ->
                                caves.stream()
                                        .map(this::map)
                                        .collect(Collectors.toList())).orElse(Collections.emptyList())
                )
                .build();
    }

    private CaveOverview map(CaveOverviewDTO caveOverviewDTO) {
        return modelMapper.map(caveOverviewDTO, CaveOverview.class);
    }
}
