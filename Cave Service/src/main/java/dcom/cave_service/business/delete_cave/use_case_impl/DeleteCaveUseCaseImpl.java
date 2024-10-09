package dcom.cave_service.business.delete_cave.use_case_impl;

import dcom.cave_service.business.delete_cave.use_case.DeleteCaveUseCase;
import dcom.cave_service.persistence.repositories.CaveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCaveUseCaseImpl implements DeleteCaveUseCase {
    private final CaveRepository caveRepository;

    @Transactional
    public boolean deleteCave(UUID id) {
        if (!caveRepository.existsById(id)) {
            return false;
        }

        caveRepository.deleteById(id);

        return true;
    }
}
