package dcom.cave_service.controllers;

import dcom.cave_service.business.create_channel.use_case.CreateChannelUseCase;
import dcom.cave_service.business.delete_chat_channel.use_case.DeleteChatChannelUseCase;
import dcom.cave_service.business.delete_voice_channel.use_case.DeleteVoiceChannelUseCase;
import dcom.cave_service.domain.requests.CreateChannelRequest;
import dcom.cave_service.domain.responses.CreateChannelResponse;
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

    @PostMapping
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody @Valid CreateChannelRequest request) {
        return ResponseEntity.ok(
                createChannelUseCase.createChannel(request)
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
