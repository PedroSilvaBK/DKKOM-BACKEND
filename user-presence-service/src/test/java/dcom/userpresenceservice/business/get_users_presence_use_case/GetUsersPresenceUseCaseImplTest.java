package dcom.userpresenceservice.business.get_users_presence_use_case;

import dcom.userpresenceservice.business.exceptions.ErrorRetrievingUserPresence;
import dcom.userpresenceservice.domain.Status;
import dcom.userpresenceservice.domain.UserPresence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUsersPresenceUseCaseImplTest {
    @Mock
    private RedisTemplate<String, UserPresence> redisTemplate;

    @Mock
    private ValueOperations<String, UserPresence> valueOperations;

    @InjectMocks
    private GetUsersPresenceUseCaseImpl getUsersPresenceUseCase;

    @Test
    void getUsersPresence() {
        List<String> userIds = List.of(
                "123e4567-e89b-42d3-a456-556642440000", "123e4567-e89b-42d3-a456-556642430000",
                "123e4567-e89b-42d3-a458-556642440000"
        );

        List<String> keys = List.of(
                "presence:123e4567-e89b-42d3-a456-556642440000", "presence:123e4567-e89b-42d3-a456-556642430000",
                "presence:123e4567-e89b-42d3-a458-556642440000"
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

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(keys)).thenReturn(List.of(userPresence1, userPresence2, userPresence3));

        List<UserPresence> userPresences = getUsersPresenceUseCase.getUsersPresence(userIds);

        assertEquals(userPresence1, userPresences.get(0));
        assertEquals(userPresence2, userPresences.get(1));
        assertEquals(userPresence3, userPresences.get(2));

        verify(redisTemplate).opsForValue();
        verify(valueOperations).multiGet(keys);
    }

    @Test
    void getUsersPresence_with_empty_ids() {
        List<String> userIds = List.of(
                "123e4567-e89b-42d3-a456-556642440000", "",
                ""
        );

        assertThrows(ErrorRetrievingUserPresence.class, () -> getUsersPresenceUseCase.getUsersPresence(userIds));
    }
}