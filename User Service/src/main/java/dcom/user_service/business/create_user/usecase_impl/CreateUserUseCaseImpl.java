package dcom.user_service.business.create_user.usecase_impl;

import dcom.user_service.business.create_user.usecase.CreateUserUseCase;
import dcom.user_service.domain.User;
import dcom.user_service.domain.requests.CreateUserRequest;
import dcom.user_service.persistence.entities.UserEntity;
import dcom.user_service.persistence.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(CreateUserRequest createUserRequest) {
        UUID uuid = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(uuid)
                .username("Username-"+uuid)
                .name(createUserRequest.getName())
                .email(createUserRequest.getEmail())
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        return User.builder()
                .email(savedUser.getEmail())
                .id(savedUser.getId().toString())
                .name(savedUser.getName())
                .username(savedUser.getUsername())
                .build();
    }
}
