package com.staccato.config;

import java.util.Arrays;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Arrays.asList(
                        new Server().url("https://stage.staccato.kr").description("Development Server URL"),
                        new Server().url("http://localhost:8080").description("Local Server URL")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Auth"))
                .components(attachBearerAuthScheme());
    }

    private Components attachBearerAuthScheme() {
        return new Components().addSecuritySchemes("Auth",
                new SecurityScheme()
                        .name("Authorization")
                        .type(Type.APIKEY)
                        .in(In.HEADER)
                        .description("Enter your token in the Authorization header"));
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("V1 API")
                .pathsToExclude("/v2/**", "/v3/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("V2 API")
                .pathsToMatch("/v2/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v3Api() {
        return GroupedOpenApi.builder()
                .group("V3 API")
                .pathsToMatch("/v3/**")
                .build();
    }
}
