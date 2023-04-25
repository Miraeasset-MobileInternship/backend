package miraeassetmobile.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Configuration
@Component
public class SwaggerConfig {
    //Spring-Doc을 이용한 swagger 설정

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1-miraeasset-mobile-internship-project") //group name
                .pathsToMatch("/api/**") // api로 시작하는 모든 API 매치
                .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {



        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("Bearer");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");



        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth",bearerAuth))
                .addSecurityItem(securityRequirement)
                .info(new Info().title("MIRAEASSET Mobile Internship Project API")
                        .description("2023 미래에셋증권 인턴십 - 모바일 개발팀 프로젝트 API 명세서입니다.")
                        .version("v0.0.1"));

    }





}
