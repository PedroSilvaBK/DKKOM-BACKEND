package dcom.cave_service.controllers;

import dcom.cave_service.business.assign_cave_role_to_member.usecase.AssignCaveRoleToMemberUseCase;
import dcom.cave_service.business.create_cave.use_case.CreateCaveUseCase;
import dcom.cave_service.business.create_cave_invite.use_case.CreateCaveInviteUseCase;
import dcom.cave_service.business.create_cave_role.use_case.CreateCaveRoleUseCase;
import dcom.cave_service.business.delete_cave.use_case.DeleteCaveUseCase;
import dcom.cave_service.business.get_cave.use_case.GetCaveBootstrapUseCase;
import dcom.cave_service.business.get_cave_members.use_case.GetCaveMembersUseCase;
import dcom.cave_service.business.get_cave_members_filtered_by_channel_use_case.GetCaveMembersFilteredByChannelUseCase;
import dcom.cave_service.business.get_cave_roles.use_case.GetCaveRolesUseCase;
import dcom.cave_service.business.get_cave_roles_overview.use_case.GetCaveRolesOverviewUseCase;
import dcom.cave_service.business.get_caves_by_user_id.use_case.GetCavesByUserIdUseCase;
import dcom.cave_service.business.get_caves_overview.use_case.GetCavesOverviewUseCase;
import dcom.cave_service.business.join_cave.use_case.JoinCaveUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.update_cave_roles.use_case.UpdateCavesRoleUseCase;
import dcom.cave_service.domain.CaveBootStrapInformation;
import dcom.cave_service.domain.CaveRole;
import dcom.cave_service.domain.requests.AssignRoleRequest;
import dcom.cave_service.domain.requests.CreateCaveInviteRequest;
import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.requests.CreateCaveRoleRequest;
import dcom.cave_service.domain.responses.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cave")
@RequiredArgsConstructor
public class CaveController {
    private final CreateCaveUseCase createCaveUseCase;
    private final DeleteCaveUseCase deleteCaveUseCase;
    private final GetCavesOverviewUseCase getCavesOverviewUseCase;

    private final GetCaveBootstrapUseCase getCaveBootstrapUseCase;

    private final CreateCaveRoleUseCase createCaveRoleUseCase;
    private final GetCaveRolesUseCase getCaveRolesUseCase;
    private final GetCaveRolesOverviewUseCase getCaveRolesOverviewUseCase;
    private final UpdateCavesRoleUseCase updateCavesRoleUseCase;
    private final AssignCaveRoleToMemberUseCase assignCaveRoleToMemberUseCase;

    private final CreateCaveInviteUseCase createCaveInviteUseCase;
    private final JoinCaveUseCase joinCaveUseCase;

    private final GetCaveMembersUseCase getCaveMembersUseCase;
    private final GetCaveMembersFilteredByChannelUseCase getCaveMembersFilteredByChannelUseCase;

    private final GetCavesByUserIdUseCase getCavesByUserIdUseCase;

    private final RolesAndPermissionsService rolesAndPermissionsService;
    @PostMapping("/permissions/{caveId}/{memberId}")
    public void teste(@PathVariable UUID caveId, @PathVariable UUID memberId) {
        rolesAndPermissionsService.getUserMergedPermissions(memberId, caveId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaveBootStrapInformation> getCaveBootstrap(@PathVariable UUID id, @RequestHeader("X-User-Id") String authUserId) {
        return ResponseEntity.ok(getCaveBootstrapUseCase.getCaveBootstrapUseCave(id, authUserId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<String>> getCavesByUserId(@PathVariable UUID userId){
        return ResponseEntity.ok(getCavesByUserIdUseCase.getCavesIdsByUserId(userId));
    }

    @GetMapping("/{caveId}/member")
    public ResponseEntity<GetCaveMembersResponse> getCaveMembers(@PathVariable UUID caveId){
        return ResponseEntity.ok(getCaveMembersUseCase.getCaveMembers(caveId));
    }

    @GetMapping("/{caveId}/member/channel/{channelId}")
    public ResponseEntity<GetCaveMembersResponse> getCaveMembersFilteredByChannel(@PathVariable UUID caveId, @PathVariable UUID channelId){
        return ResponseEntity.ok(getCaveMembersFilteredByChannelUseCase.getCaveMembersResponse(channelId, caveId));
    }

//    @GetMapping("/overview/{userId}")
//    public ResponseEntity<GetCavesOverviewResponse> getCavesOverview(@PathVariable UUID userId,  @RequestHeader("X-User-Id") String authUserId) {
//        return ResponseEntity.ok(getCavesOverviewUseCase.getCavesOverview(userId, authUserId));
//    }

    @GetMapping("/overview/{userId}")
    public ResponseEntity<GetCavesOverviewResponse> getCavesOverview(@PathVariable UUID userId) {
        return ResponseEntity.ok(getCavesOverviewUseCase.getCavesOverview(userId));
    }

    @PostMapping
    public ResponseEntity<CreateCaveResponse> createCave(@RequestBody @Valid CreateCaveRequest request, @RequestHeader("X-User-Id") String authUserId,  @RequestHeader("X-Username") String authUsername) {
        return ResponseEntity.ok(createCaveUseCase.createCave(request, authUserId, authUsername));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCave(@PathVariable UUID id){
        return ResponseEntity.ok(deleteCaveUseCase.deleteCave(id));
    }

    @PostMapping("/{caveId}/role")
    public ResponseEntity<CreateCaveRoleResponse> createCaveRole(@PathVariable UUID caveId, @RequestBody @Valid CreateCaveRoleRequest request) {
        assert caveId != null;

        request.setCaveId(caveId);

        return ResponseEntity.ok(createCaveRoleUseCase.createCaveRole(request));
    }

    @PatchMapping("/{caveId}/role")
    public ResponseEntity<Boolean> updateCaveRoles(@RequestBody List<CaveRole> caveRoles){
        return ResponseEntity.ok(updateCavesRoleUseCase.updateCaveRole(caveRoles));
    }

    @GetMapping("/{caveId}/role")
    public ResponseEntity<GetCaveRolesResponse> getCaveRoles(@PathVariable UUID caveId) {
        return ResponseEntity.ok(getCaveRolesUseCase.getCaveRoles(caveId));
    }

    @GetMapping("/{caveId}/role/overview")
    public ResponseEntity<GetCaveRolesOverviewResponse> getCaveRolesOverview(@PathVariable UUID caveId) {
        return ResponseEntity.ok(getCaveRolesOverviewUseCase.getCaveRoles(caveId));
    }

    @PostMapping("/{caveId}/invite")
    public ResponseEntity<CreateCaveInviteResponse> createCaveInvite(@PathVariable UUID caveId, @RequestBody @Valid CreateCaveInviteRequest request) {
        assert caveId != null;

        request.setCaveId(caveId);

        return ResponseEntity.ok(createCaveInviteUseCase.createCaveInvite(request));
    }

    @GetMapping("/invite/{inviteId}")
    public ResponseEntity<JoinCaveResponse> joinCave(@PathVariable UUID inviteId,  @RequestHeader("X-User-Id") String authUserId,  @RequestHeader("X-Username") String authUsername) {
        return ResponseEntity.ok(joinCaveUseCase.joinCave(inviteId, authUserId, authUsername));
    }

    @PostMapping("/{caveId}/role/assign")
    public ResponseEntity<Boolean> assignRole(@PathVariable UUID caveId, @RequestBody @Valid AssignRoleRequest request) {
        return ResponseEntity.ok(assignCaveRoleToMemberUseCase.assignRole(caveId, request));
    }
}
