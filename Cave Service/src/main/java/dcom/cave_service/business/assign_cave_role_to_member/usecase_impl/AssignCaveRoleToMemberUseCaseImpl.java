package dcom.cave_service.business.assign_cave_role_to_member.usecase_impl;

import dcom.cave_service.business.assign_cave_role_to_member.usecase.AssignCaveRoleToMemberUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.update_cave_roles.use_case_impl.UpdateCavesRolesUseCaseImpl;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.CaveRoleOverview;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.events.RoleAssignedToMember;
import dcom.cave_service.domain.requests.AssignRoleRequest;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.exceptions.MemberNotFoundException;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignCaveRoleToMemberUseCaseImpl implements AssignCaveRoleToMemberUseCase {
    private final MemberRepository memberRepository;
    private final CaveRoleRepository caveRoleRepository;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final CaveRepository caveRepository;
    private final PermissionsUtils permissionsUtils;

    private final JwtUserDetails jwtUserDetails;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public boolean assignRole(UUID caveId, AssignRoleRequest assignRoleRequest) {
        UserRolesAndPermissionsCache callerPermissions = rolesAndPermissionsService.getUserMergedPermissions(
                UUID.fromString(jwtUserDetails.getUserId()), caveId
        );
        if (!permissionsUtils.canManageRoles(callerPermissions)) {
            log.info("User {} cannot assign role in cave {}", jwtUserDetails.getUserId(), caveId);
            return false;
        }

        List<CaveRoleEntity> foundRoles = findRoles(assignRoleRequest.getRoleIds());
        if (!permissionsUtils.isOwner(callerPermissions) && !permissionsUtils.isAdmin(callerPermissions)) {
            MemberEntity caller = memberRepository.findByUserIdAndCaveEntity_Id(UUID.fromString(jwtUserDetails.getUserId()), caveId);
            CaveRoleEntity callerHighestRole = findHighestRole(caller.getRoleEntities());

            if (!canAssignRoles(callerHighestRole, foundRoles)) {
                log.info("User {} does not have sufficient permissions to assign roles to member {}",
                        jwtUserDetails.getUserId(), assignRoleRequest.getMemberId());
                return false;
            }
        }

        MemberEntity foundMember = findMember(assignRoleRequest.getMemberId());

        foundMember.setRoleEntities(new HashSet<>(foundRoles));
        MemberEntity savedMemberEntity = memberRepository.save(foundMember);

        UserRolesAndPermissionsCache updatedPermissions = rolesAndPermissionsService.forceUpdate(
                foundMember.getUserId(), savedMemberEntity.getCaveEntity().getId()
        );

        sendPermissionsUpdate(savedMemberEntity, updatedPermissions);

        sendRoleAssignedEvent(savedMemberEntity, caveId, foundRoles, updatedPermissions);

        return true;
    }


    private CaveRoleEntity findHighestRole(Set<CaveRoleEntity> roles) {
        return roles.stream()
                .min(Comparator.comparingInt(CaveRoleEntity::getPosition))
                .orElseThrow(() -> new IllegalStateException("Caller has no roles assigned"));
    }

    private boolean canAssignRoles(CaveRoleEntity callerHighestRole, List<CaveRoleEntity> targetRoles) {
        return targetRoles.stream().anyMatch(role -> role.getPosition() > callerHighestRole.getPosition());
    }

    private void sendPermissionsUpdate(MemberEntity member, UserRolesAndPermissionsCache permissions) {
        String key = member.getCaveEntity().getId() + ":" + member.getUserId();
        kafkaTemplate.send("update-user-permissions", Map.of(key, permissions));
    }

    private void sendRoleAssignedEvent(MemberEntity member, UUID caveId, List<CaveRoleEntity> roles,
                                       UserRolesAndPermissionsCache permissions) {
        List<String> visibleChannels = caveRepository.findAllTextChannelsFromCave(caveId).stream()
                .map(UUID::toString)
                .filter(channelId -> permissionsUtils.canSeeChannel(permissions, channelId))
                .toList();

        RoleAssignedToMember event = RoleAssignedToMember.builder()
                .userId(member.getUserId())
                .caveId(caveId)
                .caveRoleOverviews(roles.stream().map(this::map).toList())
                .channelsSideListsWhereUserAppears(visibleChannels)
                .build();

        kafkaTemplate.send("role-assigned-to-member", event);
    }

    private MemberEntity findMember(UUID memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("Member not found")
        );
    }

    private CaveRoleOverview map(CaveRoleEntity caveRoleEntity) {
        return CaveRoleOverview.builder()
                .id(caveRoleEntity.getId())
                .name(caveRoleEntity.getName())
                .position(caveRoleEntity.getPosition())
                .build();
    }

    private List<CaveRoleEntity> findRoles(List<UUID> roleIds) {
        return caveRoleRepository.findAllByRoleIds(roleIds.stream().toList()).orElseThrow(
                () -> new CaveNotFoundException("Cave role not found")
        );
    }
}
