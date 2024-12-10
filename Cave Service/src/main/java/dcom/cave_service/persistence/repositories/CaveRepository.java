package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.DTO.CaveOverviewDTO;
import dcom.cave_service.persistence.DTO.CaveOverviewInfo;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.entities.ChannelEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaveRepository extends JpaRepository<CaveEntity, UUID> {

    @Query("SELECT new dcom.cave_service.persistence.DTO.CaveOverviewInfo(" +
            "c.id, c.name, c.owner) " +
            "FROM CaveEntity c " +
            "LEFT JOIN VoiceChannelEntity vc ON vc.caveEntity.id = c.id " +
            "LEFT JOIN ChatChannelEntity ch ON ch.caveEntity.id = c.id " +
            "WHERE c.id = :caveId")
    List<CaveOverviewInfo> findCaveBootstrapInfo(UUID caveId);

    @Query("SELECT vc " +
            "FROM VoiceChannelEntity vc " +
            "WHERE vc.caveEntity.id = :caveId")
    List<ChannelEntity> findVoiceChannels(UUID caveId);

    @Query("SELECT ch " +
            "FROM ChatChannelEntity ch " +
            "WHERE ch.caveEntity.id = :caveId")
    List<ChannelEntity> findChatChannels(UUID caveId);

    @Query("SELECT new dcom.cave_service.persistence.DTO.CaveOverviewDTO(c.id, c.name) FROM CaveEntity c JOIN c.memberEntities m WHERE m.userId = :userId")
    Optional<List<CaveOverviewDTO>> findAllCavesByUserId(UUID userId);

    @Query("SELECT m FROM CaveEntity c " +
            "JOIN c.memberEntities m " +
            "WHERE c.id = :caveId")
    List<MemberEntity> findAllCaveMembersByCaveId(UUID caveId);

    @Query("SELECT r FROM CaveEntity c " +
            "JOIN c.memberEntities m " +
            "JOIN m.roleEntities r " +
            "WHERE c.id = :caveId AND m.userId = :userId " +
            "ORDER BY r.position ASC")
    List<CaveRoleEntity> findAllRolesByCaveIdAndMemberId(UUID caveId, UUID userId);

    @Query("SELECT cc.id FROM CaveEntity c JOIN c.chatChannelEntities cc ON cc.caveEntity.id = c.id WHERE c.id = :caveId")
    List<UUID> findAllTextChannelsFromCave(@Param("caveId") UUID caveId);

    boolean existsByOwnerAndId(UUID ownerId, UUID caveId);

    int deleteAllByOwner(UUID ownerId);
}
