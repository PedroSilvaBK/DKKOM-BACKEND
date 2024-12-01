package dcom.user_service.business.get_user.usecase_impl;

import dcom.user_service.business.exceptions.UserNotFound;
import dcom.user_service.business.get_user.usecase.GetUserUseCase;
import dcom.user_service.domain.User;
import dcom.user_service.persistence.entities.UserEntity;
import dcom.user_service.persistence.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserUseCaseImpl implements GetUserUseCase {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<User> getUser(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        log.debug("user - {} retrieved by email", email);

        return userEntity.map(entity -> modelMapper.map(entity, User.class));
    }

    @Transactional
    public User getUser(UUID id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);

        log.debug("user - {} retrieved by id", id);

        return userEntity.map(entity -> modelMapper.map(entity, User.class)).orElseThrow(() -> new UserNotFound("User not found"));
    }
}
