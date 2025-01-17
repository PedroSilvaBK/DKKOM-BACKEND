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
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {


    @GetMapping
    public ResponseEntity<String> health() {

        return ResponseEntity.ok("OK");
    }
}
