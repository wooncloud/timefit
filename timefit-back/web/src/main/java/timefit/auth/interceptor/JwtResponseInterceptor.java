package timefit.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import timefit.config.JwtConfig;

/**
 * JWT 응답 헤더 자동 설정 Interceptor
 * - Controller 에서 중복되는 JWT 헤더 설정 제거
 * - Request Attribute에 accessToken이 있으면 자동으로 응답 헤더에 설정
 */
@Slf4j
@Component
public class JwtResponseInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_ATTRIBUTE = "accessToken";

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        // Request Attribute에서 accessToken 추출
        Object accessToken = request.getAttribute(ACCESS_TOKEN_ATTRIBUTE);

        if (accessToken instanceof String) {
            // Authorization 헤더에 JWT 토큰 설정
            response.setHeader(
                    JwtConfig.AUTHORIZATION_HEADER,
                    JwtConfig.TOKEN_PREFIX + accessToken
            );

            log.debug("JWT 토큰 응답 헤더 설정 완료");
        }
    }
}