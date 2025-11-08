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
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT 토큰 인증 필터
 * 모든 요청에 대해 JWT 토큰을 검증하고 사용자 정보를 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("JWT 필터 처리: {}", requestURI);

        try {
            // 인증이 필요한 경로인지 확인
            if (requiresAuthentication(requestURI)) {
                validateTokenAndSetUser(request);
            }

            filterChain.doFilter(request, response);

        } catch (AuthException e) {
            log.warn("JWT 인증 실패: {} - {}", requestURI, e.getMessage());
            handleAuthenticationError(response, e);
        }
    }

    /**
     * 인증이 필요한 경로인지 확인
     */
    private boolean requiresAuthentication(String requestURI) {
        // 공개 API (인증 불필요)
        if (requestURI.startsWith("/api/auth/") ||
                requestURI.startsWith("/api/businesses/search") ||
                requestURI.startsWith("/api/validation/") ||
                requestURI.startsWith("/actuator/") ||
                requestURI.startsWith("/swagger-ui/") ||
                requestURI.startsWith("/v3/api-docs/") ||
                requestURI.equals("/favicon.ico")) {
            return false;
        }

        // /api/로 시작하는 경로는 인증 필요
        return requestURI.startsWith("/api/");
    }

    /**
     * 토큰 검증 및 사용자 정보 설정
     */
    private void validateTokenAndSetUser(HttpServletRequest request) {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        if (!authTokenService.isValidToken(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 토큰에서 사용자 ID 추출하여 요청에 설정
        UUID userId = authTokenService.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("token", token);

        log.debug("JWT 인증 성공: userId={}", userId);
    }

    /**
     * 요청에서 JWT 토큰 추출
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConfig.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfig.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConfig.TOKEN_PREFIX_LENGTH);
        }

        return null;
    }

    /**
     * 인증 에러 응답 처리
     */
    private void handleAuthenticationError(HttpServletResponse response, AuthException authException)
            throws IOException {

        response.setStatus(authException.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
                "{\"success\":false,\"message\":\"%s\",\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
                "인증에 실패했습니다",
                authException.getErrorCode(),
                authException.getMessage()
        );

        response.getWriter().write(errorJson);
    }
}