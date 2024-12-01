package dcom.permissionsservice.persistence.repositories;

import dcom.permissionsservice.persistence.entities.VoiceChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoiceChannelRepository extends JpaRepository<VoiceChannelEntity, UUID> {
}
