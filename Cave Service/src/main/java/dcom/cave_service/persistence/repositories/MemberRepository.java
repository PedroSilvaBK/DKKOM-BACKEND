package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
}
