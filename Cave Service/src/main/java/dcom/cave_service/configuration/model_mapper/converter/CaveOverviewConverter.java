package dcom.cave_service.configuration.model_mapper.converter;

import dcom.cave_service.domain.CaveOverview;
import dcom.cave_service.persistence.entities.CaveEntity;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CaveOverviewConverter extends AbstractConverter<CaveEntity, CaveOverview> {
    @Override
    protected CaveOverview convert(CaveEntity caveEntity) {
        return CaveOverview.builder()
                .id(caveEntity.getId())
                .name(caveEntity.getName())
                .build();
    }
}
