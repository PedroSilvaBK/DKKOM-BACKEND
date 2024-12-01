package dcom.cave_service.business.permissions_service;

import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.ChannelRepository;
import dcom.cave_service.persistence.repositories.ChannelRoleRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import dcom.sharedlibrarydcom.shared.CavePermissions;
import dcom.sharedlibrarydcom.shared.ChannelPermissionsCache;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RolesAndPermissionsService {
    private final CaveRepository caveRepository;
    private final ChannelRoleRepository channelRoleRepository;
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;

    private final RedisTemplate<String, UserRolesAndPermissionsCache> redisTemplate;

    public UserRolesAndPermissionsCache getUserChannelPermissions(UUID userId, UUID channelId){
        UserRolesAndPermissionsCache userPermissionsCache = redisTemplate.opsForValue().get(userId.toString());

        if (userPermissionsCache == null){
            UUID caveId = channelRepository.findCaveIdByChannelId(channelId);
            userPermissionsCache = getUserMergedPermissions(userId, caveId);
        }

        return userPermissionsCache;
    }

    public boolean userBelongsToCave(UUID userId, UUID caveId){
        return memberRepository.existsByUserIdAndCaveEntity_Id(userId, caveId);
    }

    public List<UserRolesAndPermissionsCache> getUsersMergedPermissions(List<UUID> userIds, UUID caveId) {
        List<UserRolesAndPermissionsCache> usersPermissionsCache = new ArrayList<>();

        for (UUID userId : userIds){
            usersPermissionsCache.add(
                    getUserMergedPermissions(userId, caveId)
            );
        }

        return usersPermissionsCache;
    }

    public Map<String, UserRolesAndPermissionsCache> aggregatePermissions(List<String> userIds, String caveId) {
        Map<String, UserRolesAndPermissionsCache> userRolesAndPermissionsCaches = new HashMap<>();
        for (String userId : userIds) {
            userRolesAndPermissionsCaches.put(
                    caveId + ":" + userId,
                    getUserMergedPermissions(UUID.fromString(userId), UUID.fromString(caveId))
            );
        }

        return userRolesAndPermissionsCaches;
    }

    public UserRolesAndPermissionsCache getUserMergedPermissions(UUID userId, UUID caveId) {
        UserRolesAndPermissionsCache userPermissionsCache = redisTemplate.opsForValue().get(userId.toString());

        if (userPermissionsCache != null) {
            return userPermissionsCache;
        }

        boolean isOwner = caveRepository.existsByOwnerAndId(userId, caveId);
        if (isOwner) {
            return UserRolesAndPermissionsCache.builder()
                    .cavePermissions(CavePermissions.OWNER)
                    .channelPermissionsCacheHashMap(new HashMap<>(0))
                    .userRoles(new ArrayList<>(0))
                    .build();
        }

        List<CaveRoleEntity> orderedCaveRoles = caveRepository.findAllRolesByCaveIdAndMemberId(caveId, userId);

        if (orderedCaveRoles.isEmpty()) {
            return UserRolesAndPermissionsCache.builder()
                    .cavePermissions(961)
                    .channelPermissionsCacheHashMap(new HashMap<>(0))
                    .userRoles(new ArrayList<>(0))
                    .build();
        }

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

        int permissionsOfHighesRole = orderedCaveRoles.getFirst().getPermissions();
        List<String> userCaveRolesIds = orderedCaveRoles.stream()
                .map(caveRoleEntity -> caveRoleEntity.getId().toString())
                .toList();


        userPermissionsCache = UserRolesAndPermissionsCache.builder()
                .cavePermissions(permissionsOfHighesRole)
                .channelPermissionsCacheHashMap(aggregatedChannelRoles)
                .userRoles(userCaveRolesIds)
                .build();

        redisTemplate.opsForValue().set(userId.toString(), userPermissionsCache);

        return userPermissionsCache;
    }


    public Map<String, UserRolesAndPermissionsCache> forceUpdate(List<String> userIds, String caveId) {
        redisTemplate.delete(userIds);
        return aggregatePermissions(userIds, caveId);
    }

    public UserRolesAndPermissionsCache forceUpdate(UUID userId, UUID caveId) {
        deletePermissionsCache(userId);
        return getUserMergedPermissions(userId, caveId);
    }

    public Boolean deletePermissionsCache(UUID userId) {
        return redisTemplate.delete(userId.toString());
    }
}
