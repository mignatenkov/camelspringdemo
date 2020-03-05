package org.example.camelspringdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Write out only non-null fields
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        objectMapper.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN);
        objectMapper.enable(com.fasterxml.jackson.core.JsonParser.Feature.IGNORE_UNDEFINED);

        // perform configuration
        return objectMapper;
    }

}
