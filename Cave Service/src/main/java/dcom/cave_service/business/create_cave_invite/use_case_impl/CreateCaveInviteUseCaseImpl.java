package dcom.cave_service.business.create_cave_invite.use_case_impl;

import dcom.cave_service.business.create_cave_invite.use_case.CreateCaveInviteUseCase;
import dcom.cave_service.domain.requests.CreateCaveInviteRequest;
import dcom.cave_service.domain.responses.CreateCaveInviteResponse;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.exceptions.InvalidExpirationDateException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import dcom.cave_service.persistence.repositories.CaveInviteRepository;
import dcom.cave_service.persistence.repositories.CaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCaveInviteUseCaseImpl implements CreateCaveInviteUseCase {
    private final CaveInviteRepository caveInviteRepository;
    private final CaveRepository caveRepository;

    @Transactional
    public CreateCaveInviteResponse createCaveInvite(CreateCaveInviteRequest createCaveInviteRequest) {

        if (createCaveInviteRequest.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            throw new InvalidExpirationDateException("Invalid expiration date");
        }

        CaveEntity caveEntity = caveRepository.findById(createCaveInviteRequest.getCaveId())
                .orElseThrow(() -> new CaveNotFoundException("Cave not found"));

        CaveInviteEntity caveInviteEntity = CaveInviteEntity.builder()
                .id(UUID.randomUUID())
                .expirationDate(createCaveInviteRequest.getExpirationDate())
                .caveEntity(caveEntity)
                .maxUses(createCaveInviteRequest.getMaxUsers())
                .build();

        CaveInviteEntity savedCaveInvite = caveInviteRepository.save(caveInviteEntity);

        return CreateCaveInviteResponse.builder()
                .id(savedCaveInvite.getId())
                .build();
    }
}
