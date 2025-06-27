package timefit.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import timefit.auth.service.AuthTokenService;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

import java.io.IOException;
import java.util.UUID;

/**
 * 임시 토큰 검증 필터 (JWT 구현 전까지 사용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    private static final String TEMP_TOKEN_HEADER = "x-client-token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.debug("요청 처리: {} {}", method, requestURI);

        try {
            // 인증이 필요한 경로인지 확인
            if (requiresAuth(requestURI)) {
                validateToken(request);
            }

        } catch (AuthException e) {
            log.warn("인증 실패: {}, URI: {}", e.getMessage(), requestURI);
            handleAuthError(response, e);
            return;
        } catch (Exception e) {
            log.error("예상치 못한 인증 오류", e);
            handleAuthError(response, new AuthException(AuthErrorCode.TOKEN_INVALID)); // AUTHENTICATION_FAILED 대신 기존 코드 사용
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증이 필요한 경로인지 확인
     */
    private boolean requiresAuth(String requestURI) {
        // 인증이 필요하지 않은 경로들
        if (requestURI.startsWith("/api/business/auth/") ||
                requestURI.startsWith("/api/customer/auth/") ||
                requestURI.equals("/api/health") ||
                requestURI.equals("/api/auth/logout") ||
                requestURI.equals("/api/auth/token/status") ||
                requestURI.startsWith("/api/validation/")) {
            return false;
        }

        // 그 외 모든 /api/ 경로는 인증 필요
        return requestURI.startsWith("/api/");
    }

    /**
     * 토큰 검증
     */
    private void validateToken(HttpServletRequest request) {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID); // TOKEN_NOT_PROVIDED 대신 기존 코드 사용
        }

        if (!authTokenService.isValidToken(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 토큰에서 사용자 ID 추출하여 요청에 설정
        UUID userId = authTokenService.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("token", token);

        log.debug("인증 성공: userId={}", userId);
    }

    /**
     * 요청에서 토큰 추출
     */
    private String extractToken(HttpServletRequest request) {
        return request.getHeader(TEMP_TOKEN_HEADER);
    }

    /**
     * 인증 에러 처리
     */
    private void handleAuthError(HttpServletResponse response, AuthException authException) throws IOException {
        response.setStatus(authException.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
                "{\"success\":false,\"message\":\"%s\",\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                "인증에 실패했습니다",
                authException.getErrorCode(),
                authException.getHttpStatus()
        );

        response.getWriter().write(errorJson);
    }
}