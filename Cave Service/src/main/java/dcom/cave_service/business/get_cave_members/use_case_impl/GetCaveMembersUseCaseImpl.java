package dcom.cave_service.business.get_cave_members.use_case_impl;

import com.fasterxml.jackson.core.type.TypeReference;
import dcom.cave_service.business.get_cave_members.use_case.GetCaveMembersUseCase;
import dcom.cave_service.domain.CaveRoleOverview;
import dcom.cave_service.domain.MemberOverview;
import dcom.cave_service.domain.UserPresence;
import dcom.cave_service.domain.responses.GetCaveMembersResponse;
import dcom.cave_service.persistence.entities.MemberEntity;
import dcom.cave_service.persistence.repositories.CaveRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCaveMembersUseCaseImpl implements GetCaveMembersUseCase {
    private final CaveRepository caveRepository;
    private final ModelMapper modelMapper;

    @Value("${user.presence.service.url}")
    private String baseUrl;

    public GetCaveMembersResponse getCaveMembers(UUID caveId) {
        List<MemberOverview> members = caveRepository.findAllCaveMembersByCaveId(caveId)
                .stream().map(this::map).toList();

        RestTemplate restTemplate = new RestTemplate();

        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/user-presence");
        uriBuilder.queryParam("userIds", members.stream().map(MemberOverview::getUserId).toList());

        List<UserPresence> usersPresence = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, null , new ParameterizedTypeReference<List<UserPresence>>() {}).getBody();

        for (int i = 0; i < members.size(); i++) {
            members.get(i).setUserStatus(usersPresence.get(i).getStatus());

            List<CaveRoleOverview> mutableRoles = new ArrayList<>(members.get(i).getRoles());
            mutableRoles.sort(Comparator.comparingInt(CaveRoleOverview::getPosition));
            members.get(i).setRoles(mutableRoles);
        }

        return GetCaveMembersResponse.builder()
                .memberOverviews(members)
                .build();
    }

    private MemberOverview map(MemberEntity memberEntity) {
        return modelMapper.map(memberEntity, MemberOverview.class);
    }
}
