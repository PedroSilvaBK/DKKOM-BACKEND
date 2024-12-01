package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.CaveInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaveInviteRepository extends JpaRepository<CaveInviteEntity, UUID> {
}
