package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.ChannelRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChannelRoleRepository extends JpaRepository<ChannelRoleEntity, UUID> {
    boolean existsByEntityIdAndChannelEntity_Id(UUID entityId, UUID channelId);
    boolean existsByEntityIdAndId(UUID entityId, UUID channelId);

    List<ChannelRoleEntity> findAllByChannelEntity_Id(UUID channelId);

    List<ChannelRoleEntity> findAllByEntityId(UUID entityId);
}
