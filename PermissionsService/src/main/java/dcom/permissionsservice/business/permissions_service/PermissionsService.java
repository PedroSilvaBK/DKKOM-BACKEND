package dcom.permissionsservice.business.permissions_service;

import dcom.permissionsservice.business.utils.PermissionsUtils;
import dcom.permissionsservice.persistence.entities.CaveRoleEntity;
import dcom.permissionsservice.persistence.entities.ChannelRoleEntity;
import dcom.permissionsservice.persistence.repositories.CaveRepository;
import dcom.permissionsservice.persistence.repositories.ChannelRepository;
import dcom.permissionsservice.persistence.repositories.ChannelRoleRepository;
import dcom.permissionsservice.persistence.repositories.MemberRepository;
import dcom.sharedlibrarydcom.shared.CavePermissions;
import dcom.sharedlibrarydcom.shared.ChannelPermissions;
import dcom.sharedlibrarydcom.shared.ChannelPermissionsCache;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionsService {
    private final CaveRepository caveRepository;
    private final ChannelRoleRepository channelRoleRepository;
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;

    private final PermissionsUtils permissionsUtils;

    private final RedisTemplate<String, Object> redisTemplate;

    public UserRolesAndPermissionsCache getUserChannelPermissions(UUID userId, UUID channelId){
        UserRolesAndPermissionsCache userPermissionsCache = (UserRolesAndPermissionsCache) redisTemplate.opsForValue().get(userId.toString());

        if (userPermissionsCache == null){
            UUID caveId = channelRepository.findCaveIdByChannelId(channelId);
            userPermissionsCache = getMergedUserPermissions(userId, caveId);
        }

        return userPermissionsCache;
    }

    public boolean userBelongsToCave(UUID userId, UUID caveId){
        return memberRepository.existsByUserIdAndCaveEntity_Id(userId, caveId);
    }

    public Map<String, UserRolesAndPermissionsCache> getMergedUserPermissions(List<String> userIds, String caveId) {
        Map<String, UserRolesAndPermissionsCache> userRolesAndPermissionsCaches = new HashMap<>();
        for (String userId : userIds) {
            userRolesAndPermissionsCaches.put(
                    caveId + ":" + userId,
                    getMergedUserPermissions(UUID.fromString(userId), UUID.fromString(caveId))
            );
        }

        return userRolesAndPermissionsCaches;
    }

    public UserRolesAndPermissionsCache getMergedUserPermissions(UUID userId, UUID caveId) {
        UserRolesAndPermissionsCache userPermissionsCache = (UserRolesAndPermissionsCache) redisTemplate.opsForValue().get(userId.toString());

        // return cached user permissions
        if (userPermissionsCache != null) {
            return userPermissionsCache;
        }

        // check if user is owner and return owner level permissions
        boolean isOwner = caveRepository.existsByOwnerAndId(userId, caveId);
        if (isOwner) {
            return UserRolesAndPermissionsCache.builder()
                    .cavePermissions(CavePermissions.OWNER)
                    .channelPermissionsCacheHashMap(new HashMap<>(0))
                    .userRoles(new ArrayList<>(0))
                    .build();
        }

        // fetch user's cave roles
        List<CaveRoleEntity> orderedCaveRoles = caveRepository.findAllRolesByCaveIdAndMemberId(caveId, userId);
        // if the user has no roles return default permissions
        if (orderedCaveRoles.isEmpty()) {
            return UserRolesAndPermissionsCache.builder()
                    .cavePermissions(961)
                    .channelPermissionsCacheHashMap(new HashMap<>(0))
                    .userRoles(new ArrayList<>(0))
                    .build();
        }

        // fetch overwritten roles for channels
        // channel overwrites can be by a cave role or a member
        // so I can allow a specific user to do something within a channel or a role a user has
        // in this case member channel overwrites have priority over cave role overwrites
        List<ChannelRoleEntity> allRolesChannelPermissions = channelRoleRepository.findAllByEntityId(orderedCaveRoles.getFirst().getId());
        List<ChannelRoleEntity> allMembersChannelPermissions = channelRoleRepository.findAllByEntityId(userId);

        Map<String, ChannelPermissionsCache> aggregatedChannelRoles = new HashMap<>();

        for (ChannelRoleEntity memberPermission : allMembersChannelPermissions) {
            String channelId = memberPermission.getChannelEntity().getId().toString();
            aggregatedChannelRoles.put(channelId, ChannelPermissionsCache.builder()
                    .allow(memberPermission.getAllow())
                    .deny(memberPermission.getDeny())
                    .build());
        }

        for (ChannelRoleEntity rolePermission : allRolesChannelPermissions) {
            String channelId = rolePermission.getChannelEntity().getId().toString();
            aggregatedChannelRoles.putIfAbsent(channelId, ChannelPermissionsCache.builder()
                    .allow(rolePermission.getAllow())
                    .deny(rolePermission.getDeny())
                    .build());
        }

        // Since im taking a hierarchical approach the only role permissions that matter are the highest one.
        int permissionsOfHighesRole = orderedCaveRoles.getFirst().getPermissions();
        List<String> userCaveRolesIds = orderedCaveRoles.stream()
                .map(caveRoleEntity -> caveRoleEntity.getId().toString())
                .toList();


        userPermissionsCache = UserRolesAndPermissionsCache.builder()
                .cavePermissions(permissionsOfHighesRole)
                .channelPermissionsCacheHashMap(aggregatedChannelRoles)
                .userRoles(userCaveRolesIds)
                .build();

        // cache new permissions
        redisTemplate.opsForValue().set(userId.toString(), userPermissionsCache);


        return userPermissionsCache;
    }

    public Map<String, UserRolesAndPermissionsCache> forceUpdate(List<String> userIds, String caveId) {
        redisTemplate.delete(userIds);
        return getMergedUserPermissions(userIds, caveId);
    }

    public void forceUpdate(UUID userId, UUID caveId) {
        deletePermissionsCache(userId);
        getMergedUserPermissions(userId, caveId);
    }

    public boolean canSendMessage(UUID userId, UUID channelId) {
        UserRolesAndPermissionsCache userPermissionsCache = getUserChannelPermissions(userId, channelId);

        return permissionsUtils.checkChannelPermission(userPermissionsCache.getCavePermissions(),
                channelId,
                userPermissionsCache.getChannelPermissionsCacheHashMap(),
                CavePermissions.SEND_MESSAGES,
                ChannelPermissions.SEND_MESSAGES
        );
    }

    public boolean canSeeChannel(UUID userId, UUID channelId) {
        UserRolesAndPermissionsCache userPermissionsCache = getUserChannelPermissions(userId, channelId);

        return permissionsUtils.checkChannelPermission(userPermissionsCache.getCavePermissions(),
                channelId,
                userPermissionsCache.getChannelPermissionsCacheHashMap(),
                CavePermissions.SEE_CHANNELS,
                ChannelPermissions.SEE_CHANNEL
        );
    }

    public void deletePermissionsCache(UUID userId) {
        redisTemplate.delete(userId.toString());
    }
}
