package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.CaveRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaveRoleRepository extends JpaRepository<CaveRoleEntity, UUID> {
    List<CaveRoleEntity> findAllByCaveEntity_IdOrderByPositionAsc(UUID id);

    @Query("SELECT c.name FROM CaveRoleEntity c WHERE c.id IN :ids")
    List<String> findAllRoleNameById(List<UUID> ids);

    int countAllByCaveEntity_Id(UUID id);

    @Query("SELECT c FROM CaveRoleEntity c WHERE c.id IN :ids")
    Optional<List<CaveRoleEntity>> findAllByRoleIds(List<UUID> ids);

}
