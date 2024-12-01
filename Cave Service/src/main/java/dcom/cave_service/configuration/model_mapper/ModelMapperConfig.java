package dcom.cave_service.configuration.model_mapper;

import dcom.cave_service.configuration.model_mapper.converter.CaveInviteConverter;
import dcom.cave_service.configuration.model_mapper.converter.CaveOverviewConverter;
import dcom.cave_service.configuration.model_mapper.converter.MemberEntityToMemberOverviewMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {
    private final CaveInviteConverter caveInviteConverter;
    private final CaveOverviewConverter caveOverviewConverter;
    private final MemberEntityToMemberOverviewMapper memberEntityToMemberOverviewMapper;
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(caveInviteConverter);
        modelMapper.addConverter(caveOverviewConverter);
        modelMapper.addConverter(memberEntityToMemberOverviewMapper);

        return modelMapper;
    }
}
