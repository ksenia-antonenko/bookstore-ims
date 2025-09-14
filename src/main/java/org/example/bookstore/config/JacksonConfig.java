package org.example.bookstore.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.ALWAYS)
            .modulesToInstall(new JsonNullableModule());
    }
}
