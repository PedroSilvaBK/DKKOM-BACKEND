package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    boolean existsByUserIdAndCaveEntity_Id(UUID userId, UUID caveEntityId);

    MemberEntity findByUserIdAndCaveEntity_Id(UUID userId, UUID caveEntityId);

    @Query("SELECT m.username FROM MemberEntity m where m.userId IN :userIds")
    List<String> findAllMemberNameByUserId(List<UUID> userIds);

    @Query("SELECT m.caveEntity.id FROM MemberEntity m where m.userId = :userId")
    List<String> findAllCavesByUserId(UUID userId);

    @Query("SELECT r FROM MemberEntity m " +
            "JOIN m.roleEntities r " +
            "WHERE m.userId = :userId")
    List<CaveRoleEntity> findAllRolesByUserId(UUID userId);

    @Query("SELECT m.userId FROM MemberEntity m JOIN m.roleEntities r WHERE r.id = :roleId")
    List<UUID> findAllUserIsdByRoleId(UUID roleId);

    List<MemberEntity> findAllByUserId(UUID userId);

}
