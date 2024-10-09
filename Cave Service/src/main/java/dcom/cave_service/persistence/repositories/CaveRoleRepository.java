package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.CaveRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaveRoleRepository extends JpaRepository<CaveRoleEntity, UUID> {
}
