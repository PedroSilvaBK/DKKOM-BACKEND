package dcom.cave_service.business.join_cave.use_case_impl;

import dcom.cave_service.business.join_cave.use_case.JoinCaveUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.configuration.UUIDGenerator;
import dcom.cave_service.domain.CaveInvite;
import dcom.cave_service.domain.events.UserJoinedCave;
import dcom.cave_service.domain.responses.JoinCaveResponse;
import dcom.cave_service.exceptions.InvalidCaveInviteException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveInviteRepository;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JoinCaveUseCaseImpl implements JoinCaveUseCase {
    private final MemberRepository memberRepository;
    private final CaveInviteRepository caveInviteRepository;
    private final ModelMapper modelMapper;

    private final CaveRepository caveRepository;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final PermissionsUtils permissionsUtils;
    private final UUIDGenerator uuidGenerator;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    //TODO this needs to trigger some event saying that a user joined to update others interfaces
    public JoinCaveResponse joinCave(UUID inviteId, String authUserId, String authUsername) {
        CaveInviteEntity caveInviteEntity = caveInviteRepository.findById(inviteId)
                .orElseThrow(() -> new InvalidCaveInviteException("Invalid invite ID"));

        CaveInvite caveInvite = modelMapper.map(caveInviteEntity, CaveInvite.class);

        if (!caveInvite.validateInvite()) {
            throw new InvalidCaveInviteException("Invalid invite");
        }

        if (memberRepository.existsByUserIdAndCaveEntity_Id(UUID.fromString(authUserId), caveInviteEntity.getCaveEntity().getId()))
        {
            throw new InvalidCaveInviteException("User already is part of the cave");
        }

        UUID caveId = caveInviteEntity.getCaveEntity().getId();

        MemberEntity memberEntity = MemberEntity.builder()
                .id(uuidGenerator.generateUUID())
                .username(authUsername)
                .userId(UUID.fromString(authUserId))
                .caveEntity(CaveEntity.builder()
                        .id(caveId)
                        .build())
                .joinedAt(LocalDateTime.now())
                .build();

        caveInviteEntity.increaseInviteUses();

        caveInviteRepository.saveAndFlush(caveInviteEntity);

        List<String> channels = caveRepository.findAllTextChannelsFromCave(caveId).stream().map(UUID::toString).filter(string ->
                permissionsUtils.canSeeChannel(rolesAndPermissionsService.getUserMergedPermissions(UUID.fromString(authUserId), caveId), string)
        ).toList();
        kafkaTemplate.send("user-joined-cave", UserJoinedCave.builder()
                        .memberId(memberEntity.getId())
                        .userId(memberEntity.getUserId())
                        .username(memberEntity.getUsername())
                        .channelsUserIsVisible(channels)
                .build());

        memberRepository.save(memberEntity);
        return JoinCaveResponse.builder()
                .caveId(caveInvite.getCaveId())
                .build();
    }
}
