package com.hackerrank.weather.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /*
    Weather API
     */
    @Bean
    public GroupedOpenApi weatherApi() {
        final String[] packagesToScan = {"com.hackerrank.weather.controller"};
        return GroupedOpenApi
                .builder()
                .group("Weather API")
                .packagesToScan(packagesToScan)
                .pathsToMatch("/weather/**")
                .addOpenApiCustomiser(weatherApiCustomizer())
                .build();
    }

    private OpenApiCustomiser weatherApiCustomizer() {
        return openAPI -> openAPI
                .info(new Info()
                        .title("Weather API")
                        .description("Weather services using OpenAPI")
                        .version("3.0.0"));
    }

    @Bean
    public OpenAPI weatherOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Weather API").description(
                        "This is a sample Spring Boot RESTful service using springdoc-openapi and OpenAPI 3."));
    }

}