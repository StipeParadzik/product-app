package com.ingemark.productapp.app.util.modelmapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration
{
    @Bean
    public ModelMapper modelMapper(List<MappingConfiguration> mappingConfigurations)
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT);
        if (mappingConfigurations != null)
        {
            mappingConfigurations.forEach(mappingConfiguration -> {
                if (mappingConfiguration.getSourceToDestinationMapper() != null)
                {
                    modelMapper.typeMap(mappingConfiguration.getSourceType(), mappingConfiguration.getDestinationType())
                        .addMappings(mappingConfiguration.getSourceToDestinationMapper());
                }
                if (mappingConfiguration.getDestinationToSourceMapper() != null)
                {
                    modelMapper.typeMap(mappingConfiguration.getDestinationType(), mappingConfiguration.getSourceType())
                        .addMappings(mappingConfiguration.getDestinationToSourceMapper());
                }
            });
        }
        return modelMapper;
    }
}