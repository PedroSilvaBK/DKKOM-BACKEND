package dcom.permissionsservice.controllers;

import dcom.permissionsservice.business.permissions_service.PermissionsService;
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
    private final PermissionsService permissionsService;

    @GetMapping("/{userId}/cave/{caveId}")
    public ResponseEntity<UserRolesAndPermissionsCache> getUserCavePermissions(@PathVariable UUID userId, @PathVariable UUID caveId){
        return ResponseEntity.ok(permissionsService.getMergedUserPermissions(userId, caveId));
    }

    @GetMapping("/{userId}/cave/{caveId}/exists")
    public ResponseEntity<Boolean> getUserBelongsToCave(@PathVariable UUID userId, @PathVariable UUID caveId){
        return ResponseEntity.ok(permissionsService.userBelongsToCave(userId, caveId));
    }

    @GetMapping("/{userId}/channel/{channelId}")
    public ResponseEntity<UserRolesAndPermissionsCache> getUserCavePermissionsByChannel(@PathVariable UUID userId, @PathVariable UUID channelId){
        return ResponseEntity.ok(permissionsService.getUserChannelPermissions(userId, channelId));
    }

    @GetMapping("/{userId}/channel/{channelId}/send-messages")
    public ResponseEntity<Boolean> canUserSendMessageToChannel(@PathVariable UUID userId, @PathVariable UUID channelId) {
        return ResponseEntity.ok(permissionsService.canSendMessage(userId, channelId));
    }

    @GetMapping("/{userId}/channel/{channelId}/see-channel")
    public ResponseEntity<Boolean> canUserSeeChannel(@PathVariable UUID userId, @PathVariable UUID channelId) {
        return ResponseEntity.ok(permissionsService.    canSeeChannel(userId, channelId));
    }
}
