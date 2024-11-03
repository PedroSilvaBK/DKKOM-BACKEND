package dcom.cave_service.business.join_cave.use_case_impl;

import dcom.cave_service.business.join_cave.use_case.JoinCaveUseCase;
import dcom.cave_service.domain.CaveInvite;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.responses.JoinCaveResponse;
import dcom.cave_service.exceptions.IdMismatchException;
import dcom.cave_service.exceptions.InvalidCaveInviteException;
import dcom.cave_service.persistence.entities.CaveEntity;
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
    private final JwtUserDetails jwtUserDetails;

    @Transactional
    //TODO this needs to trigger some event saying that a user joined to update others interfaces
    public JoinCaveResponse joinCave(UUID inviteId) {
        CaveInviteEntity caveInviteEntity = caveInviteRepository.findById(inviteId)
                .orElseThrow(() -> new InvalidCaveInviteException("Invalid invite ID"));

        CaveInvite caveInvite = modelMapper.map(caveInviteEntity, CaveInvite.class);

        if (!caveInvite.validateInvite()) {
            throw new InvalidCaveInviteException("Invalid invite");
        }

        if (memberRepository.existsByUserIdAndCaveEntity_Id(UUID.fromString(jwtUserDetails.getUserId()), caveInviteEntity.getCaveEntity().getId()))
        {
            throw new InvalidCaveInviteException("User already is part of the cave");
        }

        MemberEntity memberEntity = MemberEntity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.fromString(jwtUserDetails.getUserId()))
                .caveEntity(CaveEntity.builder()
                        .id(caveInviteEntity.getCaveEntity().getId())
                        .build())
                .joinedAt(LocalDateTime.now())
                .build();

        caveInviteEntity.increaseInviteUses();

        caveInviteRepository.saveAndFlush(caveInviteEntity);

        memberRepository.save(memberEntity);
        return JoinCaveResponse.builder()
                .caveId(caveInvite.getCaveId())
                .build();
    }
}
