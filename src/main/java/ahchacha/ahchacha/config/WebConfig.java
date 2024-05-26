package ahchacha.ahchacha.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 더 넓은 패스 매핑
                .allowedOrigins(
                        "http://localhost:3000/",
                        "https://app.ahchacha.site/"  // 추가된 도메인
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("Content-Type", "Authorization")  // 필요한 헤더 추가
                .allowCredentials(true);
    }
}
