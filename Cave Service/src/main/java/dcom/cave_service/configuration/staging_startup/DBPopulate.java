package dcom.cave_service.configuration.staging_startup;


import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.ChatChannelEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Profile("staging")
@RequiredArgsConstructor
public class DBPopulate implements CommandLineRunner {
    private final CaveRepository caveRepository;


    @Override
    public void run(String... args) throws Exception {
        CaveEntity caveEntity1 = CaveEntity.builder()
                .id(UUID.randomUUID())
                .owner(UUID.fromString("4333bfbb-071e-495a-bd59-6b5c67a627b0"))
                .name("staging-cave-test")
                .build();

        caveEntity1.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(UUID.randomUUID())
                                .userId(UUID.fromString("4333bfbb-071e-495a-bd59-6b5c67a627b0"))
                                .username("staging-account")
                                .caveEntity(caveEntity1)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        caveEntity1.setChatChannelEntities(
                List.of(
                        ChatChannelEntity.builder()
                                .id(UUID.fromString("4213afbb-071e-495a-bd59-6b5c67a627b0"))
                                .caveEntity(caveEntity1)
                                .name("general")
                                .build()
                )
        );

        CaveEntity caveEntity2 = CaveEntity.builder()
                .id(UUID.randomUUID())
                .owner(UUID.randomUUID())
                .name("staging-cave-test-2")
                .build();

        caveEntity2.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(UUID.randomUUID())
                                .userId(UUID.fromString("4333bfbb-071e-495a-bd59-6b5c67a627b0"))
                                .username("staging-account")
                                .caveEntity(caveEntity2)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        CaveEntity caveEntity3 = CaveEntity.builder()
                .id(UUID.randomUUID())
                .owner(UUID.randomUUID())
                .name("staging-cave-test-3")
                .build();

        caveEntity3.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(UUID.randomUUID())
                                .userId(UUID.fromString("4333bfbb-071e-495a-bd59-6b5c67a627b0"))
                                .username("staging-account")
                                .caveEntity(caveEntity3)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        caveRepository.save(caveEntity1);
        caveRepository.save(caveEntity2);
        caveRepository.save(caveEntity3);



    }
}
