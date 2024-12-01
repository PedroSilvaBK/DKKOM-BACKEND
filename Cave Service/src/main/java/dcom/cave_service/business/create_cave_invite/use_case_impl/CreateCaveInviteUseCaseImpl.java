package dcom.cave_service.business.create_cave_invite.use_case_impl;

import dcom.cave_service.business.create_cave_invite.use_case.CreateCaveInviteUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.utils.PermissionsUtils;
import dcom.cave_service.domain.CaveInviteExpiration;
import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.domain.requests.CreateCaveInviteRequest;
import dcom.cave_service.domain.responses.CreateCaveInviteResponse;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import dcom.cave_service.persistence.repositories.CaveInviteRepository;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
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

    private final JwtUserDetails jwtUserDetails;
    private final RolesAndPermissionsService rolesAndPermissionsService;
    private final PermissionsUtils permissionsUtils;

    @Transactional
    public CreateCaveInviteResponse createCaveInvite(CreateCaveInviteRequest createCaveInviteRequest) {
        if (!caveRepository.existsById(createCaveInviteRequest.getCaveId()))
        {
            throw new CaveNotFoundException("Cave not found");
        }

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = rolesAndPermissionsService.getUserMergedPermissions(UUID.fromString(jwtUserDetails.getUserId()), createCaveInviteRequest.getCaveId());
        if (!permissionsUtils.canCreateInvite(userRolesAndPermissionsCache))
        {
            throw new Unauthorized("Unauthorized");
        }

        LocalDateTime expirationDate = processInviteExpirationDate(createCaveInviteRequest.getCaveInviteExpiration());

        CaveInviteEntity caveInviteEntity = CaveInviteEntity.builder()
                .id(UUID.randomUUID())
                .expirationDate(expirationDate)
                .caveEntity(
                        CaveEntity.builder()
                                .id(createCaveInviteRequest.getCaveId())
                                .build()
                )
                .maxUses(createCaveInviteRequest.getMaxUses())
                .build();

        CaveInviteEntity savedCaveInvite = caveInviteRepository.save(caveInviteEntity);

        return CreateCaveInviteResponse.builder()
                .id(savedCaveInvite.getId())
                .build();
    }

    private LocalDateTime processInviteExpirationDate(CaveInviteExpiration expirationOption) {
        switch (expirationOption){
            case THIRTY_MINUTES ->{
                return LocalDateTime.now().plusMinutes(30);
            }
            case ONE_HOUR -> {
                return LocalDateTime.now().plusHours(1);
            }
            case SIX_HOURS -> {
                return LocalDateTime.now().plusHours(6);
            }
            case TWELVE_HOURS -> {
                return LocalDateTime.now().plusHours(12);
            }
            case ONE_DAY -> {
                return LocalDateTime.now().plusDays(1);
            }
            case SEVEN_DAYS -> {
                return LocalDateTime.now().plusDays(7);
            }
            case NEVER -> {
                return LocalDateTime.now().plusYears(999);
            }
        }

        return LocalDateTime.now();
    }
}
