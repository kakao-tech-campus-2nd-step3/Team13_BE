package dbdr.global.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
			.allowedOrigins("*") // 모든 도메인 허용
			.allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드 지정
			.allowedHeaders("*") // 모든 헤더 허용
			.allowCredentials(true); // 인증 정보 허용
	}
}
