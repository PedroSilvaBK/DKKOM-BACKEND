package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<ChannelEntity, UUID> {
    @Query("SELECT c.id FROM ChannelEntity c WHERE c.caveEntity.id = :caveId")
    List<UUID> findChannelsIdsByCaveId(UUID caveId);

    @Query("SELECT c.caveEntity.id FROM ChannelEntity c WHERE c.id = :channelId")
    UUID findCaveIdByChannelId(UUID channelId);
}
