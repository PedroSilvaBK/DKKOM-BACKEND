package dcom.user_service.business.get_user.usecase_impl;

import dcom.user_service.business.get_user.usecase.GetUserUseCase;
import dcom.user_service.domain.User;
import dcom.user_service.persistence.entities.UserEntity;
import dcom.user_service.persistence.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GetUserUseCaseImpl implements GetUserUseCase {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Optional<User> getUser(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        return userEntity.map(entity -> modelMapper.map(entity, User.class));
    }
}
