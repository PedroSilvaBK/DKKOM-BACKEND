package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.CaveInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaveInviteRepository extends JpaRepository<CaveInviteEntity, UUID> {
}
