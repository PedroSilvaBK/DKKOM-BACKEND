package dcom.cave_service.configuration.model_mapper.converter;

import dcom.cave_service.domain.CaveRoleOverview;
import dcom.cave_service.domain.MemberOverview;
import dcom.cave_service.persistence.entities.MemberEntity;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityToMemberOverviewMapper extends AbstractConverter<MemberEntity, MemberOverview> {
    @Override
    protected MemberOverview convert(MemberEntity memberEntity) {
        return MemberOverview.builder()
                .id(memberEntity.getId())
                .userId(memberEntity.getUserId())
                .username(memberEntity.getUsername())
                .roles(
                        memberEntity.getRoleEntities().stream().map(entity -> CaveRoleOverview.builder()
                                        .name(entity.getName())
                                        .position(entity.getPosition())
                                        .id(entity.getId())
                                .build()
                        )
                                .toList()
                )
                .build();
    }
}
