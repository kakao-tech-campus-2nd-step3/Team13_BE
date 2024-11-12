package dbdr.global.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("*") // 모든 Origin 허용
			.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메서드
			.allowedHeaders("Authorization", "Content-Type") // 허용할 헤더
			.allowCredentials(true) // 인증 정보 포함 허용
			.exposedHeaders("Authorization") // 노출할 헤더
			.maxAge(3600); // 캐시 유지 시간 (초)
	}
}
