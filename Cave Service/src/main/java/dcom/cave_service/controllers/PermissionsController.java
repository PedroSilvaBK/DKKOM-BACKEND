package dcom.cave_service.controllers;

import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final RolesAndPermissionsService rolesAndPermissionsService;

    @GetMapping("/{userId}/cave/{caveId}")
    public ResponseEntity<UserRolesAndPermissionsCache> getUserCavePermissions(@PathVariable UUID userId, @PathVariable UUID caveId){
        return ResponseEntity.ok(rolesAndPermissionsService.getUserMergedPermissions(userId, caveId));
    }

    @GetMapping("/{userId}/cave/{caveId}/exists")
    public ResponseEntity<Boolean> getUserBelongsToCave(@PathVariable UUID userId, @PathVariable UUID caveId){
        return ResponseEntity.ok(rolesAndPermissionsService.userBelongsToCave(userId, caveId));
    }

    @GetMapping("/{userId}/channel/{channelId}")
    public ResponseEntity<UserRolesAndPermissionsCache> getUserCavePermissionsByChannel(@PathVariable UUID userId, @PathVariable UUID channelId){
        return ResponseEntity.ok(rolesAndPermissionsService.getUserChannelPermissions(userId, channelId));
    }
}
