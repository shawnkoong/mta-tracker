package com.nyctransittracker.mainapp.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    @Bean
    GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }

}
