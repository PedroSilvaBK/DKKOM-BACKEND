package dcom.messaging_service.controllers;

import dcom.messaging_service.business.Unauthorized;
import dcom.messaging_service.business.create_message.CreateMessageUseCase;
import dcom.messaging_service.business.export_user_messages.usecase.ExportUserMessagesUseCase;
import dcom.messaging_service.business.get_messages.use_case.GetMessagesUseCase;
import dcom.messaging_service.domain.requests.CreateMessageRequest;
import dcom.messaging_service.domain.requests.GetMessagesRequest;
import dcom.messaging_service.domain.responses.GetMessagesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class Controller {
    private final GetMessagesUseCase getMessagesUseCase;
    private final CreateMessageUseCase createMessageUseCase;
    private final ExportUserMessagesUseCase exportUserMessagesUseCase;

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

    @GetMapping("/export-messages/{userId}")
    public ResponseEntity<FileSystemResource> downloadMessages(@PathVariable UUID userId, @RequestHeader("X-User-Id") String authUserId) {
        try {
            File jsonFile = exportUserMessagesUseCase.exportMessages(userId, authUserId);
            FileSystemResource resource = new FileSystemResource(jsonFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + jsonFile.getName());
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Unauthorized e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
