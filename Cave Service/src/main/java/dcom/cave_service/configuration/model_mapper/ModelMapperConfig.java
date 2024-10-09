package dcom.cave_service.configuration.model_mapper;

import dcom.cave_service.configuration.model_mapper.converter.CaveInviteConverter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {
    private final CaveInviteConverter caveInviteConverter;
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(caveInviteConverter);

        return modelMapper;
    }
}
