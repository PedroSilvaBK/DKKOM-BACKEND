package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import dcom.cave_service.persistence.entities.ChatChannelEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class CaveInviteRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CaveInviteRepository caveInviteRepository;

    @BeforeEach
    void setUp() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        CaveEntity caveEntityExpected = CaveEntity.builder()
                .id(defaultUUID)
                .owner(UUID.randomUUID())
                .name("test-cave")
                .build();

        caveEntityExpected.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(defaultUUID)
                                .userId(UUID.randomUUID())
                                .username("username")
                                .caveEntity(caveEntityExpected)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        caveEntityExpected.setChatChannelEntities(
                List.of(
                        ChatChannelEntity.builder()
                                .id(UUID.randomUUID())
                                .caveEntity(caveEntityExpected)
                                .name("general")
                                .build()
                )
        );


        entityManager.persist(caveEntityExpected);


        UUID inviteId = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        caveInviteRepository.save(
                CaveInviteEntity.builder()
                        .id(inviteId)
                        .expirationDate(LocalDateTime.of(2024, 1, 1, 12, 0))
                        .maxUses(10)
                        .inviteUses(0)
                        .caveEntity(caveEntityExpected)
                        .build()
        );
    }

    @Test
    void getAllCaveInvites() {
        List<CaveInviteEntity> caveInvites = caveInviteRepository.findAll();

        assertEquals(caveInvites.size(), 1);
    }

    @Test
    void findCaveInviteById() {
        UUID inviteId = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        Optional<CaveInviteEntity> caveInviteEntity = caveInviteRepository.findById(inviteId);
        if (caveInviteEntity.isEmpty()) {
            assertEquals(0, 1);
        }

        assertEquals(caveInviteEntity.get().getId(), inviteId);
    }
}