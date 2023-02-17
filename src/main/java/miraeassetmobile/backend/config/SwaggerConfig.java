package miraeassetmobile.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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
        return new OpenAPI()
                .info(new Info().title("MIRAEASSET Mobile Internship Project API")
                        .description("2023 미래에셋증권 인턴십 - 모바일 개발팀 프로젝트 API 명세서입니다.")
                        .version("v0.0.1"));
    }




}
