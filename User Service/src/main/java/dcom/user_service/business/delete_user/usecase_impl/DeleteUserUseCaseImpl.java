package dcom.user_service.business.delete_user.usecase_impl;

import dcom.user_service.business.delete_user.usecase.DeleteUserUseCase;
import dcom.user_service.business.exceptions.UnauthorizedActionException;
import dcom.user_service.business.exceptions.UserNotFound;
import dcom.user_service.domain.JwtUserDetails;
import dcom.user_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {
    private final UserRepository userRepository;
    private final JwtUserDetails jwtUserDetails;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public boolean deleteUser(UUID userId) {
        if (!userId.toString().equals(jwtUserDetails.getUserId())) {
            log.warn("user: {} tried to delete user: {}", userId, jwtUserDetails.getUserId());
            throw new UnauthorizedActionException("you cannot delete another user");
        }

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            log.debug("user: {} does not exist", userId);
            throw new UserNotFound("user could not be found");
        }

        userRepository.deleteById(userId);
        log.info("user: {} deleted", userId);

        kafkaTemplate.send("delete-user-topic", userId.toString());
        log.debug("topic sent");

        return true;
    }
}
