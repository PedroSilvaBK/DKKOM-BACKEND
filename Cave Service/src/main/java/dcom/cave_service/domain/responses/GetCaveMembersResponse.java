package dcom.cave_service.domain.responses;

import dcom.cave_service.domain.MemberOverview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCaveMembersResponse {
    private List<MemberOverview> memberOverviews;
}
