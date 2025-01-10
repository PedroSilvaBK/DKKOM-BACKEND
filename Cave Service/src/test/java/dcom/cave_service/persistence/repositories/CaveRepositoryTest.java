package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.DTO.CaveOverviewDTO;
import dcom.cave_service.persistence.DTO.CaveOverviewInfo;
import dcom.cave_service.persistence.entities.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class CaveRepositoryTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CaveRepository caveRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID ownerId = UUID.fromString("113e4567-e89b-42d3-a456-556642440000");
        UUID user1 = UUID.fromString("113e4567-e89b-42d3-a456-556642840000");
        UUID user2 = UUID.fromString("113e4567-d89b-42d3-a456-556642440000");
        UUID member1 = UUID.fromString("113e4567-e89b-42d3-a456-566842440000");
        UUID member2 = UUID.fromString("116e4567-e89b-42d3-a456-566842440000");
        UUID chatChannelId = UUID.fromString("143e4567-e89b-42d3-a456-556642440000");
        UUID voiceChannelId = UUID.fromString("423e4567-e89b-42d3-a456-556642440000");
        UUID voiceChannelId2 = UUID.fromString("423e4567-e89a-42d3-a456-556642440000");


        CaveEntity caveEntityExpected = CaveEntity.builder()
                .id(defaultUUID)
                .owner(ownerId)
                .name("test-cave")
                .build();

        caveEntityExpected.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(defaultUUID)
                                .userId(ownerId)
                                .username("username")
                                .caveEntity(caveEntityExpected)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        caveEntityExpected.setChatChannelEntities(
                List.of(
                        ChatChannelEntity.builder()
                                .id(chatChannelId)
                                .caveEntity(caveEntityExpected)
                                .name("general")
                                .build()
                )
        );

        caveEntityExpected.setVoiceChannelEntities(
                List.of(
                        VoiceChannelEntity.builder()
                                .id(voiceChannelId)
                                .caveEntity(caveEntityExpected)
                                .name("general-voice")
                                .build(),
                        VoiceChannelEntity.builder()
                                .id(voiceChannelId2)
                                .caveEntity(caveEntityExpected)
                                .name("general-voice-2")
                                .build()
                )
        );

        caveRepository.save(caveEntityExpected);

        MemberEntity memberEntity = MemberEntity.builder()
                .id(member1)
                .caveEntity(caveEntityExpected)
                .joinedAt(LocalDateTime.now())
                .userId(user1)
                .username("username")
                .nickname("nickname")
                .build();

        MemberEntity memberEntity2 = MemberEntity.builder()
                .id(member2)
                .caveEntity(caveEntityExpected)
                .joinedAt(LocalDateTime.now())
                .userId(user2)
                .username("username2")
                .nickname("nickname")
                .build();


        memberRepository.save(memberEntity);
        memberRepository.save(memberEntity2);
    }

    @Test
    void findCaveBootstrapInfo() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID ownerId = UUID.fromString("113e4567-e89b-42d3-a456-556642440000");

        CaveOverviewInfo expected = CaveOverviewInfo.builder()
                .caveId(defaultUUID)
                .caveName("test-cave")
                .owner(ownerId)
                .build();

        List<CaveOverviewInfo> caveOverviewInfo = caveRepository.findCaveBootstrapInfo(defaultUUID);

        assertEquals(expected, caveOverviewInfo.getFirst());
    }

    @Test
    void findVoiceChannels() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        List<ChannelEntity> channelList = caveRepository.findVoiceChannels(defaultUUID);

        assertEquals(2, channelList.size());
    }

    @Test
    void findChatChannels() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        List<ChannelEntity> channelList = caveRepository.findChatChannels(defaultUUID);

        assertEquals(1, channelList.size());
    }

    @Test
    void findAllCavesByUserId() {
        UUID user1 = UUID.fromString("113e4567-e89b-42d3-a456-556642840000");

        Optional<List<CaveOverviewDTO>> caves = caveRepository.findAllCavesByUserId(user1);
        if (caves.isEmpty()) {
            assertEquals(0,1);
        }

        assertEquals(1, caves.get().size());
    }

    @Test
    void findAllCaveMembersByCaveId() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        List<MemberEntity> members = caveRepository.findAllCaveMembersByCaveId(defaultUUID);

        assertEquals(3, members.size());
    }

    @Test
    void findUsernameByChannelIdAndUserId() {
        UUID chatChannelId = UUID.fromString("143e4567-e89b-42d3-a456-556642440000");
        UUID user2 = UUID.fromString("113e4567-d89b-42d3-a456-556642440000");

        String username = caveRepository.findUsernameByChannelIdAndUserId(chatChannelId, user2);

        assertEquals("username2", username);
    }

    @Test
    void findAllRolesByCaveIdAndMemberId() {
    }

    @Test
    void findAllTextChannelsFromCave() {
    }

    @Test
    void findAllVoiceChannelsFromCave() {
    }

    @Test
    void existsByOwnerAndId() {
    }

    @Test
    void deleteAllByOwner() {
    }
}