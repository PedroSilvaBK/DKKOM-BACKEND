package dcom.permissionsservice.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaveRoleRepository extends JpaRepository<dcom.permissionsservice.persistence.entities.CaveRoleEntity, UUID> {

}
