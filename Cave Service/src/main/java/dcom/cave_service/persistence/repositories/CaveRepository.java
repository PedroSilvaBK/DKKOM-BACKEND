package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.CaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaveRepository extends JpaRepository<CaveEntity, UUID> {
}
