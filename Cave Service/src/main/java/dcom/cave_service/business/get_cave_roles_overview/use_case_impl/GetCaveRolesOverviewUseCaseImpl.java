package dcom.cave_service.business.get_cave_roles_overview.use_case_impl;

import dcom.cave_service.business.get_cave_roles_overview.use_case.GetCaveRolesOverviewUseCase;
import dcom.cave_service.domain.CaveRoleOverview;
import dcom.cave_service.domain.responses.GetCaveRolesOverviewResponse;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCaveRolesOverviewUseCaseImpl implements GetCaveRolesOverviewUseCase {
    private final CaveRoleRepository caveRoleRepository;
    private final ModelMapper modelMapper;

    public GetCaveRolesOverviewResponse getCaveRoles(UUID caveId) {
        List<CaveRoleOverview> caveRoles = caveRoleRepository.findAllByCaveEntity_IdOrderByPositionAsc(caveId)
                .stream()
                .map(this::map)
                .toList();

        return GetCaveRolesOverviewResponse.builder()
                .caveRoles(caveRoles)
                .build();
    }

    private CaveRoleOverview map(CaveRoleEntity caveRoleEntity) {
        return modelMapper.map(caveRoleEntity, CaveRoleOverview.class);
    }
}
