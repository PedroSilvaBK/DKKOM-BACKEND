package dcom.cave_service.business.delete_cave.use_case_impl;

import dcom.cave_service.domain.JwtUserDetails;
import dcom.cave_service.exceptions.Unauthorized;
import dcom.cave_service.persistence.repositories.CaveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCaveUseCaseImplTest {
    @Mock
    private CaveRepository caveRepository;
    @Mock
    private JwtUserDetails jwtUserDetails;

    @InjectMocks
    private DeleteCaveUseCaseImpl deleteCaveUseCase;

    @Test
    void deleteCave() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID userId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");

        when(caveRepository.existsByOwnerAndId(userId, defaultUUID)).thenReturn(true);
        when(caveRepository.existsById(defaultUUID)).thenReturn(true);
        when(jwtUserDetails.getUserId()).thenReturn(userId.toString());
        doNothing().when(caveRepository).deleteById(defaultUUID);

        boolean actual = deleteCaveUseCase.deleteCave(defaultUUID);

        assertTrue(actual);

        verify(caveRepository, times(1)).deleteById(defaultUUID);
        verify(caveRepository, times(1)).existsById(defaultUUID);
        verify(caveRepository, times(1)).existsByOwnerAndId(userId, defaultUUID);
    }

    @Test
    void deleteCave_user_is_not_owner(){
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID userId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");

        when(caveRepository.existsByOwnerAndId(userId, defaultUUID)).thenReturn(false);
        when(jwtUserDetails.getUserId()).thenReturn(userId.toString());

        assertThrows(Unauthorized.class, () -> deleteCaveUseCase.deleteCave(defaultUUID));
    }

    @Test
    void deleteCave_cave_doenst_exist() {
        UUID defaultUUID = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID userId = UUID.fromString("124e4567-e89b-42d3-a456-556642440000");

        when(caveRepository.existsByOwnerAndId(userId, defaultUUID)).thenReturn(true);
        when(jwtUserDetails.getUserId()).thenReturn(userId.toString());
        when(caveRepository.existsById(defaultUUID)).thenReturn(false);

        boolean actual = deleteCaveUseCase.deleteCave(defaultUUID);

        assertFalse(actual);
    }
}