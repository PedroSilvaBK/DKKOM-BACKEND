package dcom.cave_service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dcom.cave_service.business.assign_cave_role_to_member.usecase.AssignCaveRoleToMemberUseCase;
import dcom.cave_service.business.create_cave.use_case.CreateCaveUseCase;
import dcom.cave_service.business.create_cave_invite.use_case.CreateCaveInviteUseCase;
import dcom.cave_service.business.create_cave_role.use_case.CreateCaveRoleUseCase;
import dcom.cave_service.business.delete_cave.use_case.DeleteCaveUseCase;
import dcom.cave_service.business.get_cave.use_case.GetCaveBootstrapUseCase;
import dcom.cave_service.business.get_cave_members.use_case.GetCaveMembersUseCase;
import dcom.cave_service.business.get_cave_members_filtered_by_channel_use_case.GetCaveMembersFilteredByChannelUseCase;
import dcom.cave_service.business.get_cave_roles.use_case.GetCaveRolesUseCase;
import dcom.cave_service.business.get_cave_roles_overview.use_case.GetCaveRolesOverviewUseCase;
import dcom.cave_service.business.get_caves_by_user_id.use_case.GetCavesByUserIdUseCase;
import dcom.cave_service.business.get_caves_overview.use_case.GetCavesOverviewUseCase;
import dcom.cave_service.business.join_cave.use_case.JoinCaveUseCase;
import dcom.cave_service.business.permissions_service.RolesAndPermissionsService;
import dcom.cave_service.business.update_cave_roles.use_case.UpdateCavesRoleUseCase;
import dcom.cave_service.domain.*;
import dcom.cave_service.domain.requests.CreateCaveInviteRequest;
import dcom.cave_service.domain.requests.CreateCaveRequest;
import dcom.cave_service.domain.requests.CreateCaveRoleRequest;
import dcom.cave_service.domain.responses.*;
import dcom.sharedlibrarydcom.shared.UserRolesAndPermissionsCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class CaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCaveUseCase createCaveUseCase;
    @MockitoBean
    private DeleteCaveUseCase deleteCaveUseCase;
    @MockitoBean
    private GetCavesOverviewUseCase getCavesOverviewUseCase;
    @MockitoBean
    private GetCaveBootstrapUseCase getCaveBootstrapUseCase;
    @MockitoBean
    private CreateCaveRoleUseCase createCaveRoleUseCase;

    @MockitoBean
    private GetCaveRolesUseCase getCaveRolesUseCase;

    @MockitoBean
    private GetCaveRolesOverviewUseCase getCaveRolesOverviewUseCase;

    @MockitoBean
    private UpdateCavesRoleUseCase updateCavesRoleUseCase;

    @MockitoBean
    private AssignCaveRoleToMemberUseCase assignCaveRoleToMemberUseCase;

    @MockitoBean
    private CreateCaveInviteUseCase createCaveInviteUseCase;

    @MockitoBean
    private JoinCaveUseCase joinCaveUseCase;

    @MockitoBean
    private GetCaveMembersUseCase getCaveMembersUseCase;

    @MockitoBean
    private GetCaveMembersFilteredByChannelUseCase getCaveMembersFilteredByChannelUseCase;


    @MockitoBean
    private GetCavesByUserIdUseCase getCavesByUserIdUseCase;

    @MockitoBean
    private RolesAndPermissionsService rolesAndPermissionsService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getCaveBootstrap() throws Exception {
        UUID caveId = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");
        UUID ownerId = UUID.fromString("113e4567-e89b-42d3-a456-556642440000");

        UserRolesAndPermissionsCache userRolesAndPermissionsCache = UserRolesAndPermissionsCache.builder()
                .userRoles(null)
                .channelPermissionsCacheHashMap(null)
                .cavePermissions(961)
                .build();

        CaveBootStrapInformation caveBootStrapInformation = CaveBootStrapInformation.builder()
                .caveId(caveId)
                .caveName("test-cave")
                .owner(ownerId)
                .voiceChannelsOverview(new ArrayList<>())
                .textChannelsOverview(new ArrayList<>())
                .userPermissionsCache(userRolesAndPermissionsCache)
                .build();

        when(getCaveBootstrapUseCase.getCaveBootstrapUseCave(caveId, ownerId.toString())).thenReturn(caveBootStrapInformation);

        mockMvc.perform(
                get("/cave/{id}", caveId)
                        .header("X-User-Id", ownerId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(caveBootStrapInformation)));

        verify(getCaveBootstrapUseCase).getCaveBootstrapUseCave(caveId, ownerId.toString());

    }

    @Test
    void getCaveMembers() throws Exception {
        UUID ownerId = UUID.fromString("113e4567-e89b-42d3-a456-556642440000");
        UUID memberId = UUID.fromString("213e4567-e89b-42d3-a456-556642440000");
        UUID caveRole = UUID.fromString("213e4564-e89b-42d3-a456-556642440000");
        UUID caveId = UUID.fromString("123e4567-e89b-42d3-a456-556642440000");

        GetCaveMembersResponse getCaveMembersResponse = GetCaveMembersResponse.builder()
                .memberOverviews(
                        List.of(
                                MemberOverview.builder()
                                        .id(memberId)
                                        .roles(List.of(
                                                CaveRoleOverview.builder()
                                                        .name("role")
                                                        .position(0)
                                                        .id(caveRole)
                                                        .build()
                                        ))
                                        .username("member")
                                        .userId(ownerId)
                                        .userStatus(UserStatus.ONLINE)
                                        .build()
                        )
                )
                .build();

        when(getCaveMembersUseCase.getCaveMembers(caveId)).thenReturn(getCaveMembersResponse);

        mockMvc.perform(
                get("/cave/{caveId}/member", caveId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getCaveMembersResponse)));


        verify(getCaveMembersUseCase).getCaveMembers(caveId);
    }

    @Test
    void getCavesOverview() throws Exception {
        UUID userId = UUID.fromString("113e4567-e89b-42d3-a456-556642440000");

        GetCavesOverviewResponse getCavesOverviewResponse = GetCavesOverviewResponse.builder()
                .caveOverviews(
                        List.of(
                                CaveOverview.builder()
                                        .name("cave 1")
                                        .id(UUID.randomUUID())
                                        .build(),
                                CaveOverview.builder()
                                        .name("cave 2")
                                        .id(UUID.randomUUID())
                                        .build()
                        )
                )
                .build();

        when(getCavesOverviewUseCase.getCavesOverview(userId, userId.toString())).thenReturn(getCavesOverviewResponse);

        mockMvc.perform(
                get("/cave/overview/{userId}", userId)
                        .header("X-User-Id", userId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(getCavesOverviewResponse)));

        verify(getCavesOverviewUseCase).getCavesOverview(userId, userId.toString());
    }

    @Test
    void createCave() throws Exception {
        UUID caveId = UUID.fromString("113e4567-e89b-42d3-a456-556642440000");
        UUID ownerId = UUID.fromString("213e4567-e89b-42d3-a456-556642440000");
        String username ="username";

        CreateCaveResponse createCaveResponse = CreateCaveResponse.builder()
                .id(caveId)
                .build();

        CreateCaveRequest createCaveRequest = CreateCaveRequest.builder()
                .ownerId(ownerId.toString())
                .name("test-cave")
                .build();

        when(createCaveUseCase.createCave(createCaveRequest, ownerId.toString(), username)).thenReturn(createCaveResponse);

        mockMvc.perform(
                post("/cave")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCaveRequest))
                        .header("X-User-Id", ownerId)
                        .header("X-Username", username)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(createCaveResponse)));

        verify(createCaveUseCase).createCave(createCaveRequest, ownerId.toString(), username);
    }

    @Test
    void createCaveRole() throws Exception {
        UUID caveRoleId = UUID.fromString("213e4567-e89b-42d3-a456-556642440000");
        UUID caveId = UUID.fromString("253e4567-e89b-42d3-a456-556642440000");

        CreateCaveRoleRequest createCaveRoleRequest = CreateCaveRoleRequest.builder()
                .caveId(caveId)
                .name("cave-role")
                .permissions(961)
                .build();

        CreateCaveRoleResponse createCaveRoleResponse =  CreateCaveRoleResponse.builder()
                .id(caveRoleId)
                .permissions(961)
                .name("cave-role")
                .build();

        when(createCaveRoleUseCase.createCaveRole(createCaveRoleRequest)).thenReturn(createCaveRoleResponse);

        mockMvc.perform(
                post("/cave/{caveId}/role", caveId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCaveRoleRequest))
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(createCaveRoleResponse)));

        verify(createCaveRoleUseCase).createCaveRole(createCaveRoleRequest);
    }

    @Test
    void createCaveInvite() throws Exception {
        UUID caveId = UUID.fromString("253e4567-e89b-42d3-a456-556642440000");
        UUID inviteId = UUID.fromString("263e4567-e89b-42d3-a456-556642440000");

        CreateCaveInviteRequest createCaveInviteRequest = CreateCaveInviteRequest.builder()
                .caveId(caveId)
                .caveInviteExpiration(CaveInviteExpiration.ONE_HOUR)
                .maxUses(10)
                .build();

        CreateCaveInviteResponse createCaveInviteResponse = CreateCaveInviteResponse.builder()
                .id(inviteId)
                .build();

        when(createCaveInviteUseCase.createCaveInvite(createCaveInviteRequest)).thenReturn(createCaveInviteResponse);

        mockMvc.perform(
                post("/cave/{caveId}/invite", caveId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createCaveInviteRequest))
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(createCaveInviteResponse)));

        verify(createCaveInviteUseCase).createCaveInvite(createCaveInviteRequest);
    }

    @Test
    void joinCave() throws Exception {
        UUID caveId = UUID.fromString("253e4567-e89b-42d3-a456-556642440000");
        UUID inviteId = UUID.fromString("263e4567-e89b-42d3-a456-556642440000");
        String userId = "263e4567-e89b-42d3-a452-556642440000";
        String username = "username";

        JoinCaveResponse joinCaveResponse = JoinCaveResponse.builder()
                .caveId(caveId)
                .build();

        when(joinCaveUseCase.joinCave(inviteId, userId, username)).thenReturn(joinCaveResponse);

        mockMvc.perform(
                get("/cave/invite/{inviteId}", inviteId)
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(joinCaveResponse)));
    }

    @Test
    void joinCave_headers_not_present() throws Exception {
        UUID caveId = UUID.fromString("253e4567-e89b-42d3-a456-556642440000");
        UUID inviteId = UUID.fromString("263e4567-e89b-42d3-a456-556642440000");
        String userId = "263e4567-e89b-42d3-a452-556642440000";
        String username = "username";

        JoinCaveResponse joinCaveResponse = JoinCaveResponse.builder()
                .caveId(caveId)
                .build();

        when(joinCaveUseCase.joinCave(inviteId, userId, username)).thenReturn(joinCaveResponse);

        mockMvc.perform(
                        get("/cave/invite/{inviteId}", inviteId)
                )
                .andExpect(status().isBadRequest());
    }
}