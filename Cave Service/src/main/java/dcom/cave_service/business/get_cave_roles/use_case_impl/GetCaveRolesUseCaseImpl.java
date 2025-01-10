package dcom.cave_service.business.get_cave_roles.use_case_impl;

import dcom.cave_service.business.get_cave_roles.use_case.GetCaveRolesUseCase;
import dcom.cave_service.domain.CaveRole;
import dcom.cave_service.domain.responses.GetCaveRolesResponse;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCaveRolesUseCaseImpl implements GetCaveRolesUseCase {
    private final CaveRoleRepository caveRoleRepository;
    private final ModelMapper modelMapper;
    private final CaveRepository caveRepository;

    public GetCaveRolesResponse getCaveRoles(UUID caveId) {
        boolean caveExists = caveRepository.existsById(caveId);
        if (!caveExists) {
            throw new CaveNotFoundException("cave not found");
        }

        List<CaveRole> caveRoles = caveRoleRepository.findAllByCaveEntity_IdOrderByPositionAsc(caveId)
                .stream()
                .map(this::map)
                .toList();

        return GetCaveRolesResponse.builder()
                .caveRoles(caveRoles)
                .build();
    }

    private CaveRole map(CaveRoleEntity caveRoleEntity) {
        return modelMapper.map(caveRoleEntity, CaveRole.class);
    }
}
