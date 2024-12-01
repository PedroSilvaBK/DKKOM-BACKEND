package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ChannelRepository extends JpaRepository<ChannelEntity, UUID> {
    @Query("SELECT c.caveEntity.id FROM ChannelEntity c WHERE c.id = :channelId")
    UUID findCaveIdByChannelId(UUID channelId);
}
