package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.ChannelRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChannelRoleRepository extends JpaRepository<ChannelRoleEntity, UUID> {
    List<ChannelRoleEntity> findAllByEntityId(UUID entityId);
}
