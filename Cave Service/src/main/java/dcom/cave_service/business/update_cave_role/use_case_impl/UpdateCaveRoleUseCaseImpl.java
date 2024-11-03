package dcom.cave_service.business.update_cave_role.use_case_impl;

import dcom.cave_service.business.update_cave_role.use_case.UpdateCaveRoleUseCase;
import dcom.cave_service.domain.CaveRole;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCaveRoleUseCaseImpl implements UpdateCaveRoleUseCase {
    private final CaveRoleRepository caveRoleRepository;
    private final CaveRepository caveRepository;

    public boolean updateCaveRole(CaveRole caveRole) {
        //TODO ADMIN CHECK
        if (!caveExists(caveRole.getCaveId())) {
            throw new CaveNotFoundException("Cave doesnt exist");
        }

        if (caveRole.getName().isEmpty()) {
            // TODO CREATE CUSTOM EXCEPTION
        }

        CaveRoleEntity oldCaveRoleEntity = findCaveRole(caveRole.getId());


        oldCaveRoleEntity.setPermissions(caveRole.getPermissions());

        caveRoleRepository.save(oldCaveRoleEntity);

        return true;
    }

    private boolean caveExists(UUID caveId) {
        return caveRepository.existsById(caveId);
    }

    private CaveRoleEntity findCaveRole(UUID caveRoleId) {
        return caveRoleRepository.findById(caveRoleId).orElseThrow(() -> new CaveNotFoundException("Cave doesnt exist"));
    }
}
