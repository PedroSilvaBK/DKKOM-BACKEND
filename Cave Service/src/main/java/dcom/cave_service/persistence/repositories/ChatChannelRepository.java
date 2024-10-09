package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.ChatChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatChannelRepository extends JpaRepository<ChatChannelEntity, UUID> {
}
