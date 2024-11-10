package dbdr.global.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 스웨거 설정
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        SecurityScheme securityScheme = new SecurityScheme()
            .name(jwt)
            .type(SecurityScheme.Type.HTTP)
            .scheme("Bearer")
            .bearerFormat("JWT");

        Components components = new Components().addSecuritySchemes(jwt, securityScheme);

        Info info = new Info()
            .title("돌봄다리 API Document")
            .version("v1.0.0")
            .description("돌봄다리 백엔드 서버 API 명세서입니다.");

        return new OpenAPI()
            .components(components)
            .info(info)
            .addSecurityItem(securityRequirement);
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
            .group("1.all")
            .displayName("All API")
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public GroupedOpenApi institutionApi() {
        return GroupedOpenApi.builder()
            .group("institution")
            .displayName("Institution API")
            .pathsToMatch("/v*/institution/**")
            .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("admin")
            .displayName("Admin API")
            .pathsToMatch("/v*/admin/**")
            .build();
    }

    @Bean
    public GroupedOpenApi guardianApi() {
        return GroupedOpenApi.builder()
            .group("guardian")
            .displayName("Guardian API")
            .pathsToMatch("/v*/guardian/**")
            .build();
    }

    @Bean
    public GroupedOpenApi careworkerApi() {
        return GroupedOpenApi.builder()
            .group("careworker")
            .displayName("Careworker API")
            .pathsToMatch("/v*/careworker/**")
            .build();
    }

    @Bean
    public GroupedOpenApi chartApi() {
        return GroupedOpenApi.builder()
            .group("chart")
            .displayName("Chart API")
            .pathsToMatch("/v*/**/chart/**")
            .build();
    }

    @Bean
    public GroupedOpenApi excelApi() {
        return GroupedOpenApi.builder()
            .group("excel")
            .displayName("Excel API")
            .pathsToMatch("/v*/excel/**")
            .build();
    }

    @Bean
    public GroupedOpenApi authentication() {
        return GroupedOpenApi.builder()
            .group("authentication")
            .displayName("Auth API")
            .pathsToMatch("/v*/auth/**")
            .build();
    }
}
