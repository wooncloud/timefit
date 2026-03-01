package timefit.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import timefit.auth.service.dto.AccessTokenClaims;
import timefit.auth.service.helper.AccessTokenBlacklist;
import timefit.auth.service.validator.TokenValidator;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JWT 토큰 인증 필터
 *
 * Spring Security Filter Chain의 일부로, 모든 HTTP 요청이 Controller에 도달하기 전에 실행됩니다.
 * Authorization 헤더에서 JWT 토큰을 추출하여 검증하고, 유효한 경우 사용자 정보를 요청에 저장합니다.
 *
 * 실행 위치:
 * HTTP 요청 → Servlet Container → [JwtAuthFilter] → SecurityFilterChain → Controller
 *
 * 주요 기능:
 * - 공개 API 여부 판단 (HTTP Method 고려한 패턴 매칭)
 * - JWT AT 추출 및 유효성 검증
 * - 블랙리스트 조회 (로그아웃된 AT 차단)
 * - 검증된 사용자 정보를 request에 저장하여 Controller에서 사용 가능하도록 처리
 * - SecurityContext에 Authentication 설정 (Spring Security 인증 통과)
 * - 인증 실패 시 즉시 401 응답 반환 (Controller까지 도달하지 않음)
 *
 * request attribute 저장 목록:
 * - "userId"               : UUID   → Controller @CurrentUserId 사용
 * - "token"                : String → 기존 호환성 유지
 * - "accessTokenJti"      : String → 로그아웃 시 블랙리스트 등록 식별자
 * - "accessTokenExpiresAt": Date   → 블랙리스트 만료 기준 시간
 *
 * 주의사항:
 * - OncePerRequestFilter를 상속하여 요청당 한 번만 실행됨
 * - 인증 실패 시 FilterChain을 중단하고 즉시 401 응답 반환
 * - Filter 레이어는 @ExceptionHandler 미적용 → 직접 401 응답 처리
 * - HTTP Method를 고려하여 GET 전용 공개 API를 정확히 판별
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;
    private final AccessTokenBlacklist accessTokenBlacklist;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 모든 HTTP Method에서 공개 API 경로 목록 (인증 불필요)
     * SecurityConfig의 permitAll() 설정과 동일하게 유지해야 함
     * AntPathMatcher를 사용하여 와일드카드 패턴 지원
     * 참고: GET 메서드만 공개인 경로는 requiresAuthentication()에서 별도 처리
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            // 인증 관련 API (회원가입, 로그인만 공개)
            "/api/auth/signup",
            "/api/auth/signin",
            "/api/auth/refresh",
            "/api/auth/health",

            // 업체 관련 공개 API
            "/api/business/search",
            "/api/business/search/**",                              // 업체 검색 (쿼리 파라미터 포함)

//            // 예약 슬롯 (GET만 공개)
//            "/api/business/*/booking-slot",
//            "/api/business/*/booking-slot/range",
//            "/api/business/*/booking-slot/menu/*",                  // GET 예약 슬롯 조회
//            "/api/business/*/booking-slot/upcoming",

            // 검증 API
            "/api/validation/**",

            // 테스트 API (개발용)
            "/api/test/**",

            // 개발/모니터링 도구
            "/actuator/**",
            "/swagger-ui/**",
            "/v1/api-docs/**",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.debug("JWT 필터 처리: {} {}", method, requestURI);

        try {
            if (requiresAuthentication(request)) {
                validateTokenAndSetUser(request);
            }

            filterChain.doFilter(request, response);

        } catch (AuthException e) {
            log.warn("JWT 인증 실패: {} {} - {}", method, requestURI, e.getMessage());
            handleAuthenticationError(response, e);
        }
    }

    /**
     * 인증이 필요한 경로인지 확인
     * HTTP Method와 URI를 모두 고려하여 인증 필요 여부를 판단합니다.
     *
     * @param request HTTP 요청 객체
     * @return 인증 필요 여부 (true: 인증 필요, false: 인증 불필요)
     */
    private boolean requiresAuthentication(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // ========== GET 메서드만 공개인 경로 확인 ==========
        if ("GET".equals(method)) {
            if (pathMatcher.match("/api/business/*", requestURI)) {
                log.debug("공개 API 접근 (GET): 업체 상세 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/operating-hours", requestURI)) {
                log.debug("공개 API 접근 (GET): 영업시간 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/menu", requestURI)) {
                log.debug("공개 API 접근 (GET): 메뉴 목록 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/menu/*", requestURI)) {
                log.debug("공개 API 접근 (GET): 메뉴 상세 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/booking-slot", requestURI)) {
                log.debug("공개 API 접근 (GET): 예약 슬롯 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/booking-slot/range", requestURI)) {
                log.debug("공개 API 접근 (GET): 기간별 슬롯 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/booking-slot/menu/*", requestURI)) {
                log.debug("공개 API 접근 (GET): 메뉴별 슬롯 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/booking-slot/upcoming", requestURI)) {
                log.debug("공개 API 접근 (GET): 향후 슬롯 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/business/*/booking-slot/**", requestURI)) {
                log.debug("공개 API 접근 (GET): 슬롯 관련 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/public/business/*/reviews", requestURI)) {
                log.debug("공개 API 접근 (GET): 업체 리뷰 목록 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/public/business/*/reviews/statistics", requestURI)) {
                log.debug("공개 API 접근 (GET): 리뷰 통계 조회 - {}", requestURI);
                return false;
            }

            if (pathMatcher.match("/api/public/**", requestURI)) {
                log.debug("공개 API 접근 (GET): 공개 API - {}", requestURI);
                return false;
            }
        }

        // ========== 모든 메서드 공개 API 확인 (PUBLIC_PATHS) ==========
        for (String publicPath : PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, requestURI)) {
                log.debug("공개 API 접근 (모든 메서드): {} - {}", method, requestURI);
                return false;
            }
        }

        boolean requiresAuth = requestURI.startsWith("/api/");
        if (requiresAuth) {
            log.debug("인증 필요 API: {} {}", method, requestURI);
        }
        return requiresAuth;
    }

    /**
     * AT 검증 → 블랙리스트 조회 → Claims를 request attribute에 저장
     *
     * 처리 순서:
     * 1. Authorization 헤더에서 AT 추출
     * 2. AT 서명 검증 + Claims 일괄 추출 (1번 파싱) - userId, jti, expiresAt
     * 3. 블랙리스트 조회 (로그아웃된 AT 차단)
     * 4. request attribute에 userId, token, accessTokenJti, accessTokenExpiresAt 저장
     * 5. SecurityContext에 Authentication 설정
     *
     * @param request HTTP 요청
     * @throws AuthException 토큰 없음, 유효하지 않음, 블랙리스트 등록됨
     */
    private void validateTokenAndSetUser(HttpServletRequest request) {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // AT 서명 검증 + Claims 일괄 추출 (userId, jti, expiresAt) - 1번 파싱
        AccessTokenClaims claims = tokenValidator.extractAccessTokenClaims(token);

        // 블랙리스트 조회 (로그아웃된 AT 차단)
        if (accessTokenBlacklist.isBlacklisted(claims.jti())) {
            log.warn("블랙리스트에 등록된 Access Token: jti={}", claims.jti());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // request attribute 저장
        request.setAttribute("userId", claims.userId());
        // 기존 호환성 유지
        request.setAttribute("token", token);
        // 로그아웃 시 블랙리스트 등록용
        request.setAttribute("accessTokenJti", claims.jti());
        // 블랙리스트 만료 기준
        request.setAttribute("accessTokenExpiresAt", claims.expiresAt());

        // SecurityContext에 Authentication 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        claims.userId(),
                        null,
                        Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("JWT 인증 성공: userId={}", claims.userId());
    }

    /**
     * 요청에서 JWT 토큰 추출
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 순수 토큰만 추출합니다.
     *
     * @param request HTTP 요청
     * @return JWT 토큰 문자열, 토큰이 없으면 null
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
     * 인증 실패 시 FilterChain을 중단하고 401 Unauthorized 응답을 직접 작성합니다.
     *
     * @param response      HTTP 응답
     * @param authException 발생한 인증 예외
     * @throws IOException 응답 작성 중 발생하는 예외
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