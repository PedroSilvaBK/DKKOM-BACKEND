package dcom.user_service.configuration.staging_startup;


import dcom.user_service.persistence.entities.UserEntity;
import dcom.user_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("staging")
@RequiredArgsConstructor
public class DBPopulate implements CommandLineRunner {
    private final UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {
        UserEntity user = UserEntity.builder()
                .id(UUID.fromString("4333bfbb-071e-495a-bd59-6b5c67a627b0"))
                .username("staging-account")
                .name("dkkom")
                .email("dkkom.fontys@gmail.com")
                .build();

        userRepository.save(user);
    }
}
