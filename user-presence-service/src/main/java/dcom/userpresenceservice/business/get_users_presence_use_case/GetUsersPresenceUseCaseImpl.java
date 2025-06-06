package dcom.userpresenceservice.business.get_users_presence_use_case;

import dcom.userpresenceservice.domain.Status;
import dcom.userpresenceservice.domain.UserPresence;
import dcom.userpresenceservice.business.exceptions.ErrorRetrievingUserPresence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUsersPresenceUseCaseImpl implements GetUsersPresenceUseCase {
    private final RedisTemplate<String, Integer> redisTemplate;

    private final static String KEY_TEMPLATE = "presence:%s";


    public List<UserPresence> getUsersPresence(List<String> userIds){
        try {
            log.info("Getting users presences for {}", userIds);
            List<String> keys = buildKeys(userIds);
            List<Integer> userPresences = redisTemplate.opsForValue().multiGet(keys);
            if (userPresences == null) {
                return fallback(userIds);
            }

            List<UserPresence> userPresenceList = new ArrayList<>();
            for(int i = 0; i < userPresences.size(); i++){
                if (userPresences.get(i) != null) {
                    userPresenceList.add(
                            UserPresence.builder()
                                    .userId(userIds.get(i))
                                    .status(Status.ONLINE)
                                    .build()
                    );
                }
                else {
                    userPresenceList.add(
                            UserPresence.builder()
                                    .userId(userIds.get(i))
                                    .status(Status.OFFLINE)
                                    .build()
                    );
                }
            }

            log.debug("Users presences for {} are {}", userIds, userPresences);

            return userPresenceList;
        } catch (Exception e) {
            log.error("there was a problem retrieving the user presence: {}", e.getMessage());
            throw new ErrorRetrievingUserPresence("there was a problem retrieving the user presence");
        }
    }

    private List<UserPresence> fallback(List<String> userIds) {
        List<UserPresence> userPresences = new ArrayList<>();

        for (String userId : userIds) {
            userPresences.add(
                    UserPresence.builder()
                            .userId(userId)
                            .status(Status.OFFLINE)
                            .build()
            );
        }
        return userPresences;
    }

    private List<String> buildKeys(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("userIds list cannot be null or empty");
        }

        return userIds.stream()
                .peek(userId -> {
                    if (userId == null || userId.isEmpty()) {
                        throw new IllegalArgumentException("User ID cannot be null or empty");
                    }
                })
                .map(userId -> String.format(KEY_TEMPLATE, userId))
                .toList();
    }

}
