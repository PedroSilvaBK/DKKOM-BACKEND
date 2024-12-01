package dcom.cave_service.business.get_caves_by_user_id.use_case_impl;

import dcom.cave_service.business.get_caves_by_user_id.use_case.GetCavesByUserIdUseCase;
import dcom.cave_service.persistence.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCavesByUserIdUseCaseImpl implements GetCavesByUserIdUseCase {
    private final MemberRepository memberRepository;

    public List<String> getCavesIdsByUserId(UUID userId) {
        return  memberRepository.findAllCavesByUserId(userId);
    }
}
