package dcom.cave_service.controllers;

import dcom.cave_service.business.create_channel.use_case.CreateChannelUseCase;
import dcom.cave_service.business.create_channel_role.use_case.CreateChannelRoleUseCase;
import dcom.cave_service.business.delete_chat_channel.use_case.DeleteChatChannelUseCase;
import dcom.cave_service.business.delete_voice_channel.use_case.DeleteVoiceChannelUseCase;
import dcom.cave_service.business.get_channel_roles.use_case.GetChannelRolesUseCase;
import dcom.cave_service.business.update_channel_role.use_case.UpdateChannelRoleUseCase;
import dcom.cave_service.domain.ChannelRole;
import dcom.cave_service.domain.requests.CreateChannelRequest;
import dcom.cave_service.domain.requests.CreateChannelRoleRequest;
import dcom.cave_service.domain.responses.CreateChannelResponse;
import dcom.cave_service.domain.responses.CreateChannelRoleResponse;
import dcom.cave_service.domain.responses.GetChannelRolesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final CreateChannelUseCase createChannelUseCase;
    private final DeleteVoiceChannelUseCase deleteVoiceChannelUseCase;
    private final DeleteChatChannelUseCase deleteChatChannelUseCase;

    private final CreateChannelRoleUseCase createChannelRoleUseCase;
    private final GetChannelRolesUseCase getChannelRolesUseCase;
    private final UpdateChannelRoleUseCase updateChannelRoleUseCase;

    @PostMapping
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody @Valid CreateChannelRequest request) {
        return ResponseEntity.ok(
                createChannelUseCase.createChannel(request)
        );
    }

    @PostMapping("/{id}/role")
    public ResponseEntity<CreateChannelRoleResponse> createChannelRole(@PathVariable UUID id, @RequestBody @Valid CreateChannelRoleRequest request) {
        return ResponseEntity.ok(
                createChannelRoleUseCase.createChannelRole(request)
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Boolean> updateChannelRole(@PathVariable UUID id, @RequestBody @Valid ChannelRole channelRole) {
        return ResponseEntity.ok(
                updateChannelRoleUseCase.updateChannelRole(channelRole)
        );
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<GetChannelRolesResponse> getChannelRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(
                getChannelRolesUseCase.getChannelRoles(id)
        );
    }

    @DeleteMapping("/voice/{id}")
    public ResponseEntity<Boolean> deleteVoiceChannel(@PathVariable UUID id) {
        return ResponseEntity.ok(
                deleteVoiceChannelUseCase.deleteChannel(id)
        );
    }

    @DeleteMapping("/chat/{id}")
    public ResponseEntity<Boolean> deleteChatChannel(@PathVariable UUID id) {
        return ResponseEntity.ok(
                deleteChatChannelUseCase.deleteChannel(id)
        );
    }
}
