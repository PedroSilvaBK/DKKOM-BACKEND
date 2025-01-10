package dcom.cave_service.business.get_cave_roles.use_case_impl;

import dcom.cave_service.domain.CaveRole;
import dcom.cave_service.domain.responses.GetCaveRolesResponse;
import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.persistence.entities.CaveRoleEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import dcom.cave_service.persistence.repositories.CaveRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCaveRolesUseCaseImplTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CaveRoleRepository caveRoleRepository;
    @Mock
    private CaveRepository caveRepository;

    @InjectMocks
    private GetCaveRolesUseCaseImpl getCaveRolesUseCase;

    @Test
    void getCaveRoles() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        CaveRoleEntity caveRoleEntity = CaveRoleEntity.builder()
                .name("role-1")
                .permissions(961)
                .position(0)
                .id(defaultUUID)
                .build();

        CaveRoleEntity caveRoleEntity2 = CaveRoleEntity.builder()
                .name("role-2")
                .permissions(961)
                .position(0)
                .id(defaultUUID)
                .build();

        CaveRole caveRole = CaveRole.builder()
                .id(defaultUUID)
                .caveId(defaultUUID)
                .name("role-1")
                .permissions(961)
                .position(0)
                .build();

        CaveRole caveRole2 = CaveRole.builder()
                .id(defaultUUID)
                .caveId(defaultUUID)
                .name("role-2")
                .permissions(961)
                .position(0)
                .build();

        GetCaveRolesResponse expected = GetCaveRolesResponse.builder()
                .caveRoles(List.of(caveRole, caveRole2))
                .build();

        when(caveRoleRepository.findAllByCaveEntity_IdOrderByPositionAsc(defaultUUID)).thenReturn(List.of(caveRoleEntity, caveRoleEntity2));
        when(modelMapper.map(caveRoleEntity, CaveRole.class)).thenReturn(caveRole);
        when(modelMapper.map(caveRoleEntity2, CaveRole.class)).thenReturn(caveRole2);
        when(caveRepository.existsById(defaultUUID)).thenReturn(true);

        GetCaveRolesResponse actual = getCaveRolesUseCase.getCaveRoles(defaultUUID);

        assertEquals(expected, actual);
        verify(caveRoleRepository, times(1)).findAllByCaveEntity_IdOrderByPositionAsc(defaultUUID);
    }

    @Test
    void getCaveRoles_with_wrong_caveid() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        when(caveRepository.existsById(defaultUUID)).thenReturn(false);

        assertThrows(CaveNotFoundException.class, () -> getCaveRolesUseCase.getCaveRoles(defaultUUID));
    }
}