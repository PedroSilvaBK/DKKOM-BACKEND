package dcom.cave_service.business.join_cave.use_case_impl;

import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.configuration.UUIDGenerator;
import dcom.cave_service.domain.Cave;
import dcom.cave_service.domain.CaveInvite;
import dcom.cave_service.domain.responses.JoinCaveResponse;
import dcom.cave_service.exceptions.InvalidCaveInviteException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveInviteRepository;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinCaveUseCaseImplTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CaveInviteRepository caveInviteRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CaveRepository caveRepository;
    @Mock
    private RolesAndPermissionsService rolesAndPermissionsService;
    @Mock
    private PermissionsUtils permissionsUtils;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private UUIDGenerator uuidGenerator;

    @InjectMocks
    private JoinCaveUseCaseImpl joinCaveUseCase;

    @Test
    void joinCave() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        String authUsername = "username";

        CaveInviteEntity caveInviteEntity = CaveInviteEntity.builder()
                .id(defaultUUID)
                .expirationDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .maxUses(10)
                .inviteUses(0)
                .caveEntity(CaveEntity.builder().id(defaultUUID).build())
                .build();

        CaveInvite caveInvite = CaveInvite.builder()
                .id(defaultUUID)
                .caveId(defaultUUID)
                .maxUses(caveInviteEntity.getMaxUses())
                .inviteUses(caveInviteEntity.getInviteUses())
                .expirationDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();

        MemberEntity newMember = MemberEntity.builder()
                .id(defaultUUID)
                .username(authUsername)
                .userId(defaultUUID)
                .caveEntity(CaveEntity.builder()
                        .id(defaultUUID)
                        .build())
                .joinedAt(LocalDateTime.now())
                .build();

        UUID channelId = UUID.fromString("133e4567-e89b-42d3-a456-556642440000");

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = UserRolesAndPermissionsCache.builder()
                .userRoles(null)
                .channelPermissionsCacheHashMap(null)
                .cavePermissions(961)
                .build();

        JoinCaveResponse expected = JoinCaveResponse.builder()
                .caveId(defaultUUID)
                .build();

        when(caveInviteRepository.findById(defaultUUID)).thenReturn(Optional.of(caveInviteEntity));
        when(uuidGenerator.generateUUID()).thenReturn(defaultUUID);
        when(modelMapper.map(caveInviteEntity, CaveInvite.class)).thenReturn(caveInvite);
        when(memberRepository.existsByUserIdAndCaveEntity_Id(defaultUUID, defaultUUID)).thenReturn(false);
        when(caveInviteRepository.saveAndFlush(caveInviteEntity)).thenReturn(caveInviteEntity);
        when(caveRepository.findAllTextChannelsFromCave(defaultUUID)).thenReturn(List.of(channelId));
        when(rolesAndPermissionsService.getUserMergedPermissions(defaultUUID, defaultUUID)).thenReturn(userRolesAndPermissionsCache);
        when(permissionsUtils.canSeeChannel(userRolesAndPermissionsCache, channelId.toString())).thenReturn(true);
        when(kafkaTemplate.send(any(), any())).thenReturn(any());
        when(memberRepository.save(newMember)).thenReturn(newMember);

        JoinCaveResponse actual = joinCaveUseCase.joinCave(defaultUUID, String.valueOf(defaultUUID), authUsername);

        assertEquals(actual, expected);
        verify(kafkaTemplate).send(any(), any());
        verify(caveRepository).findAllTextChannelsFromCave(defaultUUID);
        verify(caveInviteRepository).saveAndFlush(caveInviteEntity);
        verify(rolesAndPermissionsService).getUserMergedPermissions(defaultUUID, defaultUUID);
        verify(permissionsUtils).canSeeChannel(userRolesAndPermissionsCache, channelId.toString());

    }

    @Test
    void joinCave_with_non_existent_invite_id() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        String authUsername = "username";

        when(caveInviteRepository.findById(defaultUUID)).thenReturn(Optional.empty());

        assertThrows(InvalidCaveInviteException.class,
                () -> joinCaveUseCase.joinCave(defaultUUID, defaultUUID.toString(), authUsername)
        );
    }

    @Test
    void joinCave_with_invalid_invite() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        String authUsername = "username";

        CaveInviteEntity caveInviteEntity = CaveInviteEntity.builder()
                .id(defaultUUID)
                .expirationDate(LocalDateTime.of(2024, 1, 1, 12, 0))
                .maxUses(10)
                .inviteUses(0)
                .caveEntity(CaveEntity.builder().id(defaultUUID).build())
                .build();

        CaveInvite caveInvite = CaveInvite.builder()
                .id(defaultUUID)
                .caveId(defaultUUID)
                .maxUses(caveInviteEntity.getMaxUses())
                .inviteUses(caveInviteEntity.getInviteUses())
                .expirationDate(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

        when(caveInviteRepository.findById(defaultUUID)).thenReturn(Optional.of(caveInviteEntity));
        when(modelMapper.map(caveInviteEntity, CaveInvite.class)).thenReturn(caveInvite);

        assertThrows(InvalidCaveInviteException.class, () -> joinCaveUseCase.joinCave(defaultUUID, defaultUUID.toString(), authUsername));

    }
    @Test
    void joinCave_when_user_already_belongs() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        String authUsername = "username";

        CaveInviteEntity caveInviteEntity = CaveInviteEntity.builder()
                .id(defaultUUID)
                .expirationDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .maxUses(10)
                .inviteUses(0)
                .caveEntity(CaveEntity.builder().id(defaultUUID).build())
                .build();

        CaveInvite caveInvite = CaveInvite.builder()
                .id(defaultUUID)
                .caveId(defaultUUID)
                .maxUses(caveInviteEntity.getMaxUses())
                .inviteUses(caveInviteEntity.getInviteUses())
                .expirationDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();

        when(caveInviteRepository.findById(defaultUUID)).thenReturn(Optional.of(caveInviteEntity));
        when(modelMapper.map(caveInviteEntity, CaveInvite.class)).thenReturn(caveInvite);
        when(memberRepository.existsByUserIdAndCaveEntity_Id(defaultUUID, defaultUUID)).thenReturn(true);

        assertThrows(InvalidCaveInviteException.class, () -> joinCaveUseCase.joinCave(defaultUUID, defaultUUID.toString(), authUsername));

    }


}