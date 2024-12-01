package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.CaveEntity;
import dcom.permissionsservice.persistence.entities.CaveRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CaveRepository extends JpaRepository<CaveEntity, UUID> {
    @Query("SELECT r FROM CaveEntity c " +
            "JOIN c.memberEntities m " +
            "JOIN m.roleEntities r " +
            "WHERE c.id = :caveId AND m.userId = :userId " +
            "ORDER BY r.position ASC")
    List<CaveRoleEntity> findAllRolesByCaveIdAndMemberId(UUID caveId, UUID userId);

    boolean existsByOwnerAndId(UUID ownerId, UUID caveId);
}
