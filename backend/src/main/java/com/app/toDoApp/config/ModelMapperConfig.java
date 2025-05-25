package com.app.toDoApp.config;

import com.app.toDoApp.dto.entrada.TaskEntradaDTO;
import com.app.toDoApp.dto.salida.TaskSalidaDTO;
import com.app.toDoApp.entity.Task;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Cambiar de STRICT a LOOSE
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        // Configuración específica para TaskEntradaDTO → Task
        modelMapper.addMappings(new PropertyMap<TaskEntradaDTO, Task>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCreatedAt());
                skip(destination.getUser());
            }
        });


        return modelMapper;
    }
}
