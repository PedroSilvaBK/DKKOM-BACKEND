package dcom.cave_service.business.create_cave.use_case_impl;

import dcom.cave_service.business.create_cave.use_case.CreateCaveUseCase;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.responses.CreateCaveResponse;
import dcom.cave_service.exceptions.IdMismatchException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCaveUseCaseImpl implements CreateCaveUseCase {
    private final CaveRepository caveRepository;
    private final JwtUserDetails jwtUserDetails;

    public CreateCaveResponse createCave(CreateCaveRequest request) {
        if (!jwtUserDetails.getUserId().equals(request.getOwnerId())) {
            throw new IdMismatchException("Owner Id is a mismatch");
        }

        CaveEntity caveEntity = CaveEntity.builder()
                .id(UUID.randomUUID())
                .owner(UUID.fromString(request.getOwnerId()))
                .name(request.getName())
                .build();

        caveEntity.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(UUID.randomUUID())
                                .userId(UUID.fromString(jwtUserDetails.getUserId()))
                                .username(jwtUserDetails.getUsername())
                                .caveEntity(caveEntity)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        CaveEntity savedCaveEntity = caveRepository.save(caveEntity);

        return CreateCaveResponse.builder()
                .id(savedCaveEntity.getId())
                .build();
    }
}
