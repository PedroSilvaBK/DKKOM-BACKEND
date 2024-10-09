package dcom.cave_service.business.create_cave.use_case_impl;

import dcom.cave_service.business.create_cave.use_case.CreateCaveUseCase;
import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.responses.CreateCaveResponse;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCaveUseCaseImpl implements CreateCaveUseCase {
    private final CaveRepository caveRepository;

    public CreateCaveResponse createCave(CreateCaveRequest request) {

        CaveEntity caveEntity = CaveEntity.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .build();

        CaveEntity savedCaveEntity = caveRepository.save(caveEntity);

        return CreateCaveResponse.builder()
                .id(savedCaveEntity.getId())
                .build();
    }
}
