package dcom.messaging_service.controllers;

import dcom.messaging_service.business.create_message.CreateMessageUseCase;
import dcom.messaging_service.business.get_messages.use_case.GetMessagesUseCase;
import dcom.messaging_service.domain.requests.CreateMessageRequest;
import dcom.messaging_service.domain.requests.GetMessagesRequest;
import dcom.messaging_service.domain.responses.GetMessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class Controller {
    private final GetMessagesUseCase getMessagesUseCase;
    private final CreateMessageUseCase createMessageUseCase;

    @GetMapping("/{channelId}")
    public ResponseEntity<GetMessagesResponse> getMessages(@PathVariable String channelId, @RequestHeader("X-User-Id") String userId, @RequestParam("pageState")String pageState) {
        return ResponseEntity.ok(
                getMessagesUseCase.getMessages(
                        GetMessagesRequest.builder()
                                .channelId(channelId)
                                .pagingState(pageState)
                                .userId(userId)
                                .build()
                )
        );
    }

    @PostMapping("/{channelId}")
    @Transactional
    public ResponseEntity<Void> sendMessage(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-Username") String username, @PathVariable String channelId, @RequestBody CreateMessageRequest message) {

        createMessageUseCase.sendMessage(username, channelId, userId, message);
        return ResponseEntity.ok().build();
    }
}
