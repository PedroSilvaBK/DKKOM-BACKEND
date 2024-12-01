package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.ChatChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatChannelRepository extends JpaRepository<ChatChannelEntity, UUID> {
}
