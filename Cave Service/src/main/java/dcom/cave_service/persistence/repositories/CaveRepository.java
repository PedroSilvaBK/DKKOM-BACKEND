package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.DTO.CaveOverviewDTO;
import dcom.cave_service.persistence.DTO.CaveWithChannelInfoDTO;
import dcom.cave_service.persistence.entities.CaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaveRepository extends JpaRepository<CaveEntity, UUID> {

    @Query("SELECT new dcom.cave_service.persistence.DTO.CaveWithChannelInfoDTO(" +
            "c.id, c.name, c.owner, " +
            "vc.name, vc.id, ch.name, ch.id) " +
            "FROM CaveEntity c " +
            "LEFT JOIN VoiceChannelEntity vc ON vc.caveEntity.id = c.id " +
            "LEFT JOIN ChatChannelEntity ch ON ch.caveEntity.id = c.id " +
            "WHERE c.id = :caveId")
    List<CaveWithChannelInfoDTO> findCaveBootstrapInfo(UUID caveId);

    @Query("SELECT new dcom.cave_service.persistence.DTO.CaveOverviewDTO(c.id, c.name) FROM CaveEntity c JOIN c.memberEntities m WHERE m.userId = :userId")
    Optional<List<CaveOverviewDTO>> findAllCavesByUserId(UUID userId);
}
