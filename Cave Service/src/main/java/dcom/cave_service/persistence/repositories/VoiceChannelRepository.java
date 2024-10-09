package dcom.cave_service.persistence.repositories;

import dcom.cave_service.persistence.entities.VoiceChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoiceChannelRepository extends JpaRepository<VoiceChannelEntity, UUID> {
}
