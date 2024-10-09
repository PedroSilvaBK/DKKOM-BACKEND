package dcom.cave_service.controllers;

import dcom.cave_service.business.create_cave.use_case.CreateCaveUseCase;
import dcom.cave_service.business.create_cave_invite.use_case.CreateCaveInviteUseCase;
import dcom.cave_service.business.create_cave_role.use_case.CreateCaveRoleUseCase;
import dcom.cave_service.business.delete_cave.use_case.DeleteCaveUseCase;
import dcom.cave_service.business.join_cave.use_case.JoinCaveUseCase;
import dcom.cave_service.domain.requests.CreateCaveInviteRequest;
import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.requests.CreateCaveRoleRequest;
import dcom.cave_service.domain.requests.JoinCaveRequest;
import dcom.cave_service.domain.responses.CreateCaveInviteResponse;
import dcom.cave_service.domain.responses.CreateCaveResponse;
import dcom.cave_service.domain.responses.CreateCaveRoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cave")
@RequiredArgsConstructor
public class CaveController {
    private final CreateCaveUseCase createCaveUseCase;
    private final DeleteCaveUseCase deleteCaveUseCase;

    private final CreateCaveRoleUseCase createCaveRoleUseCase;

    private final CreateCaveInviteUseCase createCaveInviteUseCase;
    private final JoinCaveUseCase joinCaveUseCase;

    @PostMapping
    public ResponseEntity<CreateCaveResponse> createCave(@RequestBody @Valid CreateCaveRequest request) {
        return ResponseEntity.ok(createCaveUseCase.createCave(request));
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

    @PostMapping("/{caveId}/invite")
    public ResponseEntity<CreateCaveInviteResponse> createCaveRole(@PathVariable UUID caveId, @RequestBody @Valid CreateCaveInviteRequest request) {
        assert caveId != null;

        request.setCaveId(caveId);

        return ResponseEntity.ok(createCaveInviteUseCase.createCaveInvite(request));
    }

    @PostMapping("/invite")
    public ResponseEntity<Boolean> createCaveRole(@RequestBody JoinCaveRequest joinCaveRequest) {
        return ResponseEntity.ok(joinCaveUseCase.joinCave(joinCaveRequest));
    }
}
