package dcom.cave_service.business.get_cave.use_case_impl;

import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.domain.CaveBootStrapInformation;
import dcom.cave_service.domain.ChannelOverviewDTO;
import dcom.cave_service.domain.User;
import dcom.cave_service.domain.VoiceChannelOverviewDTO;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.DTO.CaveOverviewInfo;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.ChatChannelEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import javax.crypto.DecapsulateException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCaveBootstrapUseCaveImplTest {
    @Mock
    private CaveRepository caveRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RolesAndPermissionsService rolesAndPermissionsService;
    @Mock
    private RedisTemplate<String, List<User>> redisTemplate;

    @InjectMocks
    private GetCaveBootstrapUseCaveImpl getCaveBootstrapUseCaveImpl;

    @Test
    void getCaveBootstrapUseCave() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        CaveOverviewInfo caveOverviewInfo = CaveOverviewInfo.builder()
                .caveId(defaultUUID)
                .caveName("test-cave")
                .owner(defaultUUID)
                .build();

        ChannelEntity chatChannelEntity = ChatChannelEntity.builder()
                .name("chat-channel-name")
                .id(defaultUUID)
                .build();

        ChannelOverviewDTO channelOverviewDTO = ChannelOverviewDTO.builder()
                .name("chat-channel-name")
                .id(defaultUUID)
                .build();

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = UserRolesAndPermissionsCache.builder()
                .userRoles(null)
                .channelPermissionsCacheHashMap(null)
                .cavePermissions(961)
                .build();

        CaveBootStrapInformation expected = CaveBootStrapInformation.builder()
                .owner(caveOverviewInfo.getOwner())
                .caveName(caveOverviewInfo.getCaveName())
                .caveId(caveOverviewInfo.getCaveId())
                .textChannelsOverview(List.of(channelOverviewDTO))
                .voiceChannelsOverview(new ArrayList<>())
                .userPermissionsCache(userRolesAndPermissionsCache)
                .build();

        when(memberRepository.existsByUserIdAndCaveEntity_Id(defaultUUID, defaultUUID)).thenReturn(true);
        when(caveRepository.findVoiceChannels(defaultUUID)).thenReturn(new ArrayList<>());
        when(caveRepository.findChatChannels(defaultUUID)).thenReturn(List.of(chatChannelEntity));
        when(caveRepository.findCaveBootstrapInfo(defaultUUID)).thenReturn(List.of(caveOverviewInfo));
        when(rolesAndPermissionsService.forceUpdate(defaultUUID, defaultUUID)).thenReturn(userRolesAndPermissionsCache);

        CaveBootStrapInformation actual = getCaveBootstrapUseCaveImpl.getCaveBootstrapUseCave(defaultUUID, defaultUUID.toString());

        assertEquals(expected, actual);

        verify(memberRepository).existsByUserIdAndCaveEntity_Id(defaultUUID, defaultUUID);
        verify(caveRepository).findVoiceChannels(defaultUUID);
        verify(caveRepository).findChatChannels(defaultUUID);
        verify(caveRepository).findCaveBootstrapInfo(defaultUUID);

    }

    @Test
    void getCaveInformation_user_doesnt_belong_to_cave() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        when(memberRepository.existsByUserIdAndCaveEntity_Id(defaultUUID, defaultUUID)).thenReturn(false);

        assertThrows(Unauthorized.class, () -> getCaveBootstrapUseCaveImpl.getCaveBootstrapUseCave(defaultUUID, String.valueOf(defaultUUID)));
    }
}