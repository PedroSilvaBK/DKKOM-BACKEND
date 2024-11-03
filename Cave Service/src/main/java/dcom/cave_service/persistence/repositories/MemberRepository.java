package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    boolean existsByUserIdAndCaveEntity_Id(UUID userId, UUID caveEntityId);

    MemberEntity findByUserId(UUID userId);

    @Query("SELECT m.username FROM MemberEntity m where m.userId IN :userIds")
    List<String> findAllMemberNameByUserId(List<UUID> userIds);
}
