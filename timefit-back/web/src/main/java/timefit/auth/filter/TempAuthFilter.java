//package timefit.auth.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import timefit.auth.service.AuthTokenService;
//import timefit.exception.auth.AuthException;
//import timefit.exception.auth.AuthErrorCode;
//
//import java.io.IOException;
//import java.util.UUID;
//
///**
// * 임시 토큰 검증 필터
// * 역할: x-client-token 헤더 검증 및 userId 추출
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TempAuthFilter extends OncePerRequestFilter {
//
//    private final AuthTokenService authTokenService;
//    private static final String TEMP_TOKEN_HEADER = "x-client-token";
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String requestURI = request.getRequestURI();
//        String method = request.getMethod();
//
//        log.debug("AuthFilter 처리: {} {}", method, requestURI);
//
//        try {
//
////            ---------------------
//
//            // ========== 개발용 임시 설정: 모든 요청 허용 ==========
//            // 개발 단계에서는 인증 때문에 막히는 것보다 기능 개발에 집중하기 위해
//            // 모든 API 요청을 허용하도록 설정
//            // 배포 전에 아래 주석을 해제하고 이 부분은 주석 처리할 것
//
//            // 헤더에서 토큰 추출해서 있으면 사용, 없으면 더미 사용자 설정
//            String token = extractToken(request);
//            if (StringUtils.hasText(token) && authTokenService.isValidToken(token)) {
//                UUID userId = authTokenService.getUserIdFromToken(token);
//                request.setAttribute("userId", userId);
//                request.setAttribute("token", token);
//                log.debug("토큰 사용: userId={}", userId);
//            } else {
//                // 더미 사용자 ID 설정 (개발용)
//                UUID dummyUserId = UUID.fromString("00000000-0000-0000-0000-000000000001");
//                request.setAttribute("userId", dummyUserId);
//                request.setAttribute("token", "dev-dummy-token");
//                log.debug("더미 사용자 설정: userId={}", dummyUserId);
//            }
//
////            ---------------------
//
//            // 토큰 검증이 필요한 경로인지 확인
//            // if (requiresAuth(requestURI)) {
//            //     validateTokenAndSetUserId(request);
//            // }
//
//        } catch (AuthException e) {
//            log.warn("인증 실패: {}, URI: {}", e.getMessage(), requestURI);
//            handleAuthError(response, e);
//            return;
//        } catch (Exception e) {
//            log.error("예상치 못한 인증 오류", e);
//            handleAuthError(response, new AuthException(AuthErrorCode.TOKEN_INVALID));
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    /**
//     * 토큰 검증이 필요한 경로인지 확인
//     * 단순화: 공개 API만 제외, 나머지는 모두 토큰 검증
//     */
//    private boolean requiresAuth(String requestURI) {
//        // 공개 API (토큰 검증 불필요)
//        if (
//                requestURI.startsWith("/api/auth/") ||
//                requestURI.startsWith("/api/business/search") ||
//                requestURI.startsWith("/api/validation/") ||
//                requestURI.startsWith("/actuator/") ||
//                requestURI.startsWith("/swagger-ui/") ||
//                requestURI.startsWith("/v3/api-docs/")) {
//            return false;
//        }
//
//        // 나머지 모든 /api/ 경로는 토큰 검증 필요
//        return requestURI.startsWith("/api/");
//    }
//
//    /**
//     * 토큰 검증 및 userId 설정
//     */
//    private void validateTokenAndSetUserId(HttpServletRequest request) {
//        String token = extractToken(request);
//
//        if (!StringUtils.hasText(token)) {
//            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
//        }
//
//        if (!authTokenService.isValidToken(token)) {
//            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
//        }
//
//        // 토큰에서 사용자 ID 추출하여 요청에 설정
//        UUID userId = authTokenService.getUserIdFromToken(token);
//        request.setAttribute("userId", userId);
//        request.setAttribute("token", token);
//
//        log.debug("인증 성공: userId={}", userId);
//    }
//
//    /**
//     * 요청에서 토큰 추출
//     */
//    private String extractToken(HttpServletRequest request) {
//        return request.getHeader(TEMP_TOKEN_HEADER);
//    }
//
//    /**
//     * 인증 에러 처리
//     */
//    private void handleAuthError(HttpServletResponse response, AuthException authException) throws IOException {
//        response.setStatus(authException.getHttpStatus().value());
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        String errorJson = String.format(
//                "{\"success\":false,\"message\":\"%s\",\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}",
//                "인증에 실패했습니다",
//                authException.getErrorCode(),
//                authException.getHttpStatus()
//        );
//
//        response.getWriter().write(errorJson);
//    }
//}