package dcom.messaging_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {


    @GetMapping
    public ResponseEntity<String> health() {

        return ResponseEntity.ok("OK");
    }
}
