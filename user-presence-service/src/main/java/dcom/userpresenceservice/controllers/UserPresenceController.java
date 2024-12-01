package dcom.userpresenceservice.controllers;

import dcom.userpresenceservice.domain.UserPresence;
import dcom.userpresenceservice.business.get_users_presence_use_case.GetUsersPresenceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-presence")
public class UserPresenceController {
    private final GetUsersPresenceUseCase getUsersPresenceUseCase;

    @GetMapping
    public ResponseEntity<List<UserPresence>> getUsersPresence(@RequestParam("userIds") List<String> userIds) {
        return ResponseEntity.ok(
                getUsersPresenceUseCase.getUsersPresence(userIds)
        );
    }
}
