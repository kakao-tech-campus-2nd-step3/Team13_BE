package dbdr.global.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("돌봄다리 API Document")
            .version("v1.0.0")
            .description("돌봄다리 서버 API 명세서입니다.");
        return new OpenAPI()
            .components(new Components())
            .info(info);
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
    public GroupedOpenApi recipientApi() {
        return GroupedOpenApi.builder()
            .group("recipient")
            .displayName("Recipient API")
            .pathsToMatch("/v*/recipient/**")
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
}
