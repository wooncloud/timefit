package timefit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import timefit.auth.interceptor.JwtResponseInterceptor;
import timefit.common.auth.CurrentUserIdArgumentResolver;

import java.util.List;

/**
 * Spring MVC 설정
 * - ArgumentResolver 등록
 * - Interceptor 등록
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;
    private final JwtResponseInterceptor jwtResponseInterceptor;

    // Custom ArgumentResolver 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdArgumentResolver);
    }

    // Interceptor 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtResponseInterceptor)
                .addPathPatterns("/api/auth/**");  // Auth API 에만 적용
    }
}