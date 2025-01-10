package dcom.cave_service.business.create_cave.use_case_impl;

import dcom.cave_service.configuration.UUIDGenerator;
import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.responses.CreateCaveResponse;
import dcom.cave_service.exceptions.IdMismatchException;
import dcom.cave_service.persistence.entities.CaveEntity;
import dcom.cave_service.persistence.entities.ChatChannelEntity;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCaveUseCaseImplTest {
    @Mock
    private CaveRepository caveRepository;

    @Mock
    private UUIDGenerator uuidGenerator;

    @InjectMocks
    private CreateCaveUseCaseImpl createCaveUseCase;

    @Test
    void createCave() {
        String defaultUUID = "123e4567-e89b-42d3-a456-556642440000";
        CreateCaveRequest createCaveRequest = CreateCaveRequest.builder()
                .name("test-cave")
                .ownerId(defaultUUID)
                .build();

        String authUsername = "username";

        CaveEntity caveEntityExpected = CaveEntity.builder()
                .id(UUID.fromString(defaultUUID))
                .owner(UUID.fromString(createCaveRequest.getOwnerId()))
                .name(createCaveRequest.getName())
                .build();

        caveEntityExpected.setMemberEntities(
                List.of(
                        MemberEntity.builder()
                                .id(UUID.fromString(defaultUUID))
                                .userId(UUID.fromString(defaultUUID))
                                .username(authUsername)
                                .caveEntity(caveEntityExpected)
                                .joinedAt(LocalDateTime.now())
                                .build()
                )
        );

        caveEntityExpected.setChatChannelEntities(
                List.of(
                        ChatChannelEntity.builder()
                                .id(UUID.fromString(defaultUUID))
                                .caveEntity(caveEntityExpected)
                                .name("general")
                                .build()
                )
        );

        CreateCaveResponse expectedCreateCaveResponse = CreateCaveResponse.builder()
                .id(caveEntityExpected.getId())
                .build();

        when(uuidGenerator.generateUUID()).thenReturn(UUID.fromString(defaultUUID));
        when(caveRepository.save(caveEntityExpected)).thenReturn(caveEntityExpected);

        CreateCaveResponse actual = createCaveUseCase.createCave(createCaveRequest, defaultUUID, authUsername);

        assertEquals(expectedCreateCaveResponse, actual);
        verify(caveRepository).save(caveEntityExpected);
    }

    @Test
    void createCave_as_another_user() {
        String defaultUUID = "123e4567-e89b-42d3-a456-556642440000";
        CreateCaveRequest createCaveRequest = CreateCaveRequest.builder()
                .name("test-cave")
                .ownerId("123e4567-e89b-42d3-a456-556642440001")
                .build();

        String authUsername = "username";

        assertThrows(IdMismatchException.class,
                () -> createCaveUseCase.createCave(createCaveRequest, defaultUUID, authUsername));
    }
}