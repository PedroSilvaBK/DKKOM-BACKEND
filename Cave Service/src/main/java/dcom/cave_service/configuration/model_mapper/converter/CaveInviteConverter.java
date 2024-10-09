package dcom.cave_service.configuration.model_mapper.converter;

import dcom.cave_service.domain.CaveInvite;
import dcom.cave_service.persistence.entities.CaveInviteEntity;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CaveInviteConverter extends AbstractConverter<CaveInviteEntity, CaveInvite> {
    @Override
    protected CaveInvite convert(CaveInviteEntity caveInviteEntity) {
        return CaveInvite.builder()
                .id(caveInviteEntity.getId())
                .caveId(caveInviteEntity.getCaveEntity().getId())
                .expirationDate(caveInviteEntity.getExpirationDate())
                .maxUses(caveInviteEntity.getMaxUses())
                .build();
    }
}
