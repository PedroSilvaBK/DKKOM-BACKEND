package dcom.user_service.business.update_user.use_case_impl;

import dcom.user_service.business.update_user.use_case.UpdateUserUseCase;
import dcom.user_service.domain.UpdateUsername;
import dcom.user_service.domain.User;
import dcom.user_service.persistence.entities.UserEntity;
import dcom.user_service.persistence.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Transactional
    public boolean updateUser(User user) {
        if (user.getId().isEmpty()){
            log.error("User id is empty");
            return false;
        }

        UserEntity userEntity = userRepository.findById(UUID.fromString(user.getId()))
                .orElseThrow();

        userEntity.setName(user.getName());
        userEntity.setUsername(user.getUsername());

        userRepository.save(userEntity);

        kafkaTemplate.send("update-username", UpdateUsername.builder()
                        .id(userEntity.getId())
                        .username(user.getUsername())
                .build());

        return true;
    }
}
