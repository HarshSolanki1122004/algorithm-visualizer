package com.harsh.visualizer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata shown at the top of the Swagger UI page.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI visualizerOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Algorithm Visualizer API")
                .description("Computes step-by-step traces for sorting and searching algorithms")
                .version("v1")
                .contact(new Contact().name("Harsh Solanki")));
    }
}
