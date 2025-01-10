package dcom.userpresenceservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.userpresenceservice.business.get_users_presence_use_case.GetUsersPresenceUseCase;
import dcom.userpresenceservice.domain.Status;
import dcom.userpresenceservice.domain.UserPresence;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class UserPresenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetUsersPresenceUseCase getUsersPresenceUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUsersPresence() throws Exception {
        List<String> userIds = List.of(
                "123e4567-e89b-42d3-a456-556642440000", "123e4567-e89b-42d3-a456-556642430000",
                "123e4567-e89b-42d3-a458-556642440000"
        );

        UserPresence userPresence1 = UserPresence.builder()
                .userId("123e4567-e89b-42d3-a456-556642440000")
                .status(Status.OFFLINE)
                .build();
        UserPresence userPresence2 = UserPresence.builder()
                .userId("123e4567-e89b-42d3-a456-556642430000")
                .status(Status.OFFLINE)
                .build();
        UserPresence userPresence3 = UserPresence.builder()
                .userId("123e4567-e89b-42d3-a458-556642440000")
                .status(Status.OFFLINE)
                .build();

        List<UserPresence> userPresences = List.of(
                userPresence1, userPresence2, userPresence3
        );

        when(getUsersPresenceUseCase.getUsersPresence(userIds)).thenReturn(userPresences);

        mockMvc.perform(
                get("/user-presence")
                        .param("userIds", userIds.toArray(new String[0]))
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userPresences)));

        verify(getUsersPresenceUseCase).getUsersPresence(userIds);
    }
}