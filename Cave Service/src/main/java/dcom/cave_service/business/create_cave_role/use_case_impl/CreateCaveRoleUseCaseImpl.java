package dcom.cave_service.business.create_cave_role.use_case_impl;

import dcom.cave_service.business.create_cave_role.use_case.CreateCaveRoleUseCase;
import dcom.cave_service.domain.requests.CreateCaveRoleRequest;
import dcom.cave_service.domain.responses.CreateCaveRoleResponse;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCaveRoleUseCaseImpl implements CreateCaveRoleUseCase {
    private final CaveRoleRepository caveRoleRepository;
    private final CaveRepository caveRepository;

    public CreateCaveRoleResponse createCaveRole(CreateCaveRoleRequest createCaveRoleRequest) {
        CaveEntity cave = caveRepository.findById(createCaveRoleRequest.getCaveId())
                .orElseThrow(() -> new CaveNotFoundException("Cave not found"));

        CaveRoleEntity caveRoleEntity = CaveRoleEntity.builder()
                .id(UUID.randomUUID())
                .caveEntity(cave)
                .name(createCaveRoleRequest.getName())
                .permissions(createCaveRoleRequest.getPermissions())
                .build();

        CaveRoleEntity savedCaveRole = caveRoleRepository.save(caveRoleEntity);

        return CreateCaveRoleResponse.builder()
                .id(savedCaveRole.getId())
                .build();
    }
}
