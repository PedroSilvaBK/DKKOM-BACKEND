package dcom.cave_service.business.update_cave_roles.use_case_impl;

import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.update_cave_roles.use_case.UpdateCavesRoleUseCase;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.CaveRole;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.RoleUpdate;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCavesRolesUseCaseImpl implements UpdateCavesRoleUseCase {
    private final CaveRoleRepository caveRoleRepository;
    private final CaveRepository caveRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final JwtUserDetails jwtUserDetails;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final PermissionsUtils permissionsUtils;

    public boolean updateCaveRole(List<CaveRole> updatedCaveRoles) {
        if (!caveExists(updatedCaveRoles.getFirst().getCaveId())) {
            throw new CaveNotFoundException("Cave doesnt exist");
        }

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = rolesAndPermissionsService.getUserMergedPermissions(UUID.fromString(jwtUserDetails.getUserId()),updatedCaveRoles.getFirst().getCaveId());
        if (!permissionsUtils.isOwner(userRolesAndPermissionsCache))
        {
            throw new Unauthorized("Unauthorized");
        }

        if (!checkIfRolesBelongToCave(updatedCaveRoles)) {
            return false;
        }

        if (checkRoleNames(updatedCaveRoles)) {
            // TODO CREATE CUSTOM EXCEPTION
        }

        List<UUID> roleIds = updatedCaveRoles.stream().map(CaveRole::getId).toList();
        List<CaveRoleEntity> oldCaveRoleEntities = findCaveRoles(roleIds);
        oldCaveRoleEntities.sort(Comparator.comparingInt(role -> roleIds.indexOf(role.getId())));

        for (int i = 0; i < oldCaveRoleEntities.size(); i++) {
            oldCaveRoleEntities.get(i).setPermissions(updatedCaveRoles.get(i).getPermissions());
            oldCaveRoleEntities.get(i).setPosition(updatedCaveRoles.get(i).getPosition());
        }

        List<CaveRoleEntity> savedCaveRole = caveRoleRepository.saveAll(oldCaveRoleEntities);

        kafkaTemplate.send("preprocess-cave-role-update",
                savedCaveRole.stream()
                        .map(entity -> RoleUpdate.builder()
                                .caveId(entity.getCaveEntity().getId().toString())
                                .entityId(entity.getId().toString())
                                .build())
                );

        return true;
    }

    private boolean checkRoleNames(List<CaveRole> caveRoles) {
        for (CaveRole caveRole : caveRoles) {
            if (caveRole.getName().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean checkIfRolesBelongToCave(List<CaveRole> caveRoles) {
        boolean rolesBelongToCave = true;
        for (int i = 0; i < caveRoles.size(); i++) {
            if (i == caveRoles.size() - 1) {
                break;
            }

            if (!caveRoles.get(i).getCaveId().equals(caveRoles.get(i + 1).getCaveId())) {
                rolesBelongToCave = false;
                break;
            }
        }

        return rolesBelongToCave;
    }

    private boolean caveExists(UUID caveId) {
        return caveRepository.existsById(caveId);
    }

    private List<CaveRoleEntity> findCaveRoles(List<UUID> roleIds) {
        return caveRoleRepository.findAllByRoleIds(roleIds).orElseThrow(() -> new CaveNotFoundException("Cave doesnt exist"));
    }
}
