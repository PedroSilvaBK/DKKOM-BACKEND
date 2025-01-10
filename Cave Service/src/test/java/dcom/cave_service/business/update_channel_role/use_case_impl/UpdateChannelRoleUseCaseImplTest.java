package dcom.cave_service.business.update_channel_role.use_case_impl;

import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.ChannelRole;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.PermissionType;
import dcom.cave_service.exceptions.ChannelDoesntExistException;
import dcom.cave_service.exceptions.ChannelRoleDoesntExistException;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.ChannelRepository;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateChannelRoleUseCaseImplTest {
    @Mock
    private ChannelRoleRepository channelRoleRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private JwtUserDetails jwtUserDetails;
    @Mock
    private RolesAndPermissionsService rolesAndPermissionsService;
    @Mock
    private PermissionsUtils permissionsUtils;

    @InjectMocks
    private UpdateChannelRoleUseCaseImpl updateChannelRoleUseCase;

    @Test
    void updateChannelRole() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID entityId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");
        UUID jwtUser = UUID.fromString("124e4567-e89b-42d3-a456-556642440003");

        ChannelRole channelRole = ChannelRole.builder()
                .id(defaultUUID)
                .channelId(defaultUUID)
                .entityId(entityId)
                .entityName("entity-name")
                .type(PermissionType.CAVE_ROLE)
                .position(0)
                .allow(2)
                .deny(0)
                .build();

        when(jwtUserDetails.getUserId()).thenReturn(jwtUser.toString());
        when(channelRepository.existsById(defaultUUID)).thenReturn(true);
        when(channelRoleRepository.existsByEntityIdAndId(channelRole.getEntityId(), channelRole.getId())).thenReturn(true);

        UUID caveId = UUID.fromString("124e4567-e89b-42d3-a456-556642440001");
        when(channelRepository.findCaveIdByChannelId(channelRole.getChannelId())).thenReturn(caveId);

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = UserRolesAndPermissionsCache.builder()
                .userRoles(null)
                .channelPermissionsCacheHashMap(null)
                .cavePermissions(961)
                .build();

        when(rolesAndPermissionsService.getUserMergedPermissions(jwtUser, caveId)).thenReturn(userRolesAndPermissionsCache);
        when(permissionsUtils.canManageChannel(userRolesAndPermissionsCache)).thenReturn(true);

        ChannelRoleEntity savedMappedEntity = ChannelRoleEntity.builder()
                .entityName(channelRole.getEntityName())
                .id(channelRole.getId())
                .entityId(channelRole.getEntityId())
                .entityType(channelRole.getType())
                .allow(channelRole.getAllow())
                .deny(channelRole.getDeny())
                .channelEntity(ChannelEntity.builder()
                        .id(channelRole.getChannelId())
                        .caveEntity(CaveEntity.builder()
                                .id(caveId)
                                .build())
                        .build())
                .build();

        ChannelRoleEntity mappedEntity = ChannelRoleEntity.builder()
                .entityName(channelRole.getEntityName())
                .id(channelRole.getId())
                .entityId(channelRole.getEntityId())
                .entityType(channelRole.getType())
                .allow(channelRole.getAllow())
                .deny(channelRole.getDeny())
                .channelEntity(ChannelEntity.builder()
                        .id(channelRole.getChannelId())
                        .build())
                .build();


        when(channelRoleRepository.save(mappedEntity)).thenReturn(savedMappedEntity);
        when(kafkaTemplate.send(any(), any())).thenReturn(any());

        boolean actual = updateChannelRoleUseCase.updateChannelRole(channelRole);

        assertTrue(actual);
    }

    @Test
    void updateChannelRole_channelDoesNotExist() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID entityId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");

        ChannelRole channelRole = ChannelRole.builder()
                .id(defaultUUID)
                .channelId(defaultUUID)
                .entityId(entityId)
                .entityName("entity-name")
                .type(PermissionType.CAVE_ROLE)
                .position(0)
                .allow(2)
                .deny(0)
                .build();

        when(channelRepository.existsById(defaultUUID)).thenReturn(false);

        assertThrows(ChannelDoesntExistException.class, () -> updateChannelRoleUseCase.updateChannelRole(channelRole));
    }

    @Test
    void updateChannelRole_channel_role_and_entity_dont_exist() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID entityId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");

        ChannelRole channelRole = ChannelRole.builder()
                .id(defaultUUID)
                .channelId(defaultUUID)
                .entityId(entityId)
                .entityName("entity-name")
                .type(PermissionType.CAVE_ROLE)
                .position(0)
                .allow(2)
                .deny(0)
                .build();

        when(channelRepository.existsById(defaultUUID)).thenReturn(true);
        when(channelRoleRepository.existsByEntityIdAndId(entityId, defaultUUID)).thenReturn(false);

        assertThrows(ChannelRoleDoesntExistException.class, () -> updateChannelRoleUseCase.updateChannelRole(channelRole));
    }

    @Test
    void updateChannelRole_user_doesnt_have_permission() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID entityId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");
        UUID jwtUser = UUID.fromString("124e4567-e89b-42d3-a456-556642440003");

        ChannelRole channelRole = ChannelRole.builder()
                .id(defaultUUID)
                .channelId(defaultUUID)
                .entityId(entityId)
                .entityName("entity-name")
                .type(PermissionType.CAVE_ROLE)
                .position(0)
                .allow(2)
                .deny(0)
                .build();

        when(jwtUserDetails.getUserId()).thenReturn(jwtUser.toString());
        when(channelRepository.existsById(defaultUUID)).thenReturn(true);
        when(channelRoleRepository.existsByEntityIdAndId(channelRole.getEntityId(), channelRole.getId())).thenReturn(true);

        UUID caveId = UUID.fromString("124e4567-e89b-42d3-a456-556642440001");
        when(channelRepository.findCaveIdByChannelId(channelRole.getChannelId())).thenReturn(caveId);

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = UserRolesAndPermissionsCache.builder()
                .userRoles(null)
                .channelPermissionsCacheHashMap(null)
                .cavePermissions(961)
                .build();

        when(rolesAndPermissionsService.getUserMergedPermissions(jwtUser, caveId)).thenReturn(userRolesAndPermissionsCache);
        when(permissionsUtils.canManageChannel(userRolesAndPermissionsCache)).thenReturn(false);
        when(permissionsUtils.isAdmin(userRolesAndPermissionsCache)).thenReturn(false);

        assertThrows(Unauthorized.class, () -> updateChannelRoleUseCase.updateChannelRole(channelRole));
    }
}