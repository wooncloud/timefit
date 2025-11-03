package timefit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 최소 설정
 * - 자동 생성 문서를 먼저 확인하기 위한 기본 설정만 포함
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 기본 정보만 설정
                .info(new Info()
                        .title("Timefit API")
                        .version("1.0.0")
                        .description("Timefit 예약 관리 시스템 API"))

                // JWT 보안 스키마 (전역 적용)
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))

                // 모든 API에 JWT 인증 필요 표시
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt"));
    }
}