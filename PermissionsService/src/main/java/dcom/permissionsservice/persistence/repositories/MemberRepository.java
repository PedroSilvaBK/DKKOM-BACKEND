package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    boolean existsByUserIdAndCaveEntity_Id(UUID userId, UUID caveEntityId);
}
