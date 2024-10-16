package dcom.cave_service.business.join_cave.use_case_impl;

import dcom.cave_service.business.join_cave.use_case.JoinCaveUseCase;
import dcom.cave_service.domain.CaveInvite;
import dcom.cave_service.domain.requests.JoinCaveRequest;
import dcom.cave_service.exceptions.InvalidCaveInviteException;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveInviteRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JoinCaveUseCaseImpl implements JoinCaveUseCase {
    private final MemberRepository memberRepository;
    private final CaveInviteRepository caveInviteRepository;
    private final ModelMapper modelMapper;

    @Transactional
    //TODO this needs to trigger some event saying that a user joined to update others interfaces
    public boolean joinCave(JoinCaveRequest joinCaveRequest) {
        CaveInviteEntity caveInviteEntity = caveInviteRepository.findById(joinCaveRequest.getInviteId())
                .orElseThrow(() -> new InvalidCaveInviteException("Invalid invite ID"));

        CaveInvite caveInvite = modelMapper.map(caveInviteEntity, CaveInvite.class);

        if (!caveInvite.validateInvite()) {
            throw new InvalidCaveInviteException("Invalid invite");
        }

        MemberEntity memberEntity = MemberEntity.builder()
                .id(UUID.randomUUID())
                .userId(joinCaveRequest.getUserId())
                .caveEntity(caveInviteEntity.getCaveEntity())
                .joinedAt(LocalDateTime.now())
                .build();

        memberRepository.save(memberEntity);
        return true;
    }
}
