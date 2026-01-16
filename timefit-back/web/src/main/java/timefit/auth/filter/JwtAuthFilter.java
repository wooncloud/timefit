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
import timefit.auth.service.validator.TokenValidator;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
 * - 공개 API 여부 판단 (PUBLIC_PATHS 패턴 매칭)
 * - JWT 토큰 추출 및 유효성 검증
 * - 검증된 사용자 ID를 request에 저장하여 Controller에서 사용 가능하도록 처리
 * - SecurityContext에 Authentication 설정 (Spring Security 인증 통과)
 * - 인증 실패 시 즉시 401 응답 반환 (Controller까지 도달하지 않음)
 *
 * 책임 범위:
 * - HTTP 요청에서 JWT 토큰 추출
 * - JWT 토큰 유효성 검증
 * - 토큰에서 사용자 정보(userId) 추출 및 요청에 저장
 * - Spring Security 인증 설정
 * - 인증 에러 발생 시 401 응답 처리
 *
 * 동작 순서:
 * 1. HTTP 요청 수신
 * 2. 요청 URI가 공개 API인지 확인 (PUBLIC_PATHS 패턴 매칭)
 * 3. 인증 필요 시: Authorization 헤더에서 JWT 토큰 추출
 * 4. TokenValidator로 토큰 검증
 * 5. 토큰에서 userId 추출하여 request.setAttribute("userId", userId) 저장
 * 6. SecurityContext에 Authentication 설정
 * 7. 다음 필터 체인으로 요청 전달
 *
 * SecurityConfig와의 관계:
 * - 이 필터는 SecurityFilterChain보다 먼저 실행됨
 * - PUBLIC_PATHS는 SecurityConfig의 permitAll() 설정과 일치해야 함
 * - 이 필터에서 userId를 추출하고 Authentication을 설정하면, Spring Security의 .authenticated() 체크를 통과
 * - Controller에서 @CurrentUserId로 사용 가능
 *
 * 주의사항:
 * - OncePerRequestFilter를 상속하여 요청당 한 번만 실행됨
 * - 인증 실패 시 FilterChain을 중단하고 즉시 401 응답 반환
 * - SecurityContext에 Authentication을 설정해야 Spring Security가 인증된 것으로 판단
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 공개 API 경로 목록 (인증 불필요)
     * SecurityConfig의 permitAll() 설정과 동일하게 유지해야 함
     * AntPathMatcher를 사용하여 와일드카드 패턴 지원
     *
     * 공개 API 목록:
     * 1. /api/auth/signup, /api/auth/signin - 회원가입, 로그인
     * 2. /api/business/search/** - 업체 검색 (쿼리 파라미터 포함)
     * 3. /api/business/{businessId} (GET만, SecurityConfig에서 HTTP 메서드로 구분)
     * 4. /api/business/{businessId}/booking-slot/menu/{menuId} (GET만)
     * 5. /api/validation/** - 검증 API
     * 6. 개발/모니터링 도구
     *
     * 참고: GET 메서드만 공개인 경우, SecurityConfig에서 HttpMethod로 구분하므로
     *      여기서는 경로만 체크하고, Spring Security가 최종 판단
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            // 인증 관련 API (회원가입, 로그인만 공개)
            "/api/auth/signup",
            "/api/auth/signin",
            "/api/auth/refresh",
            "/api/auth/health",

            // 업체 관련 공개 API
            "/api/business/search/**",                              // 업체 검색 (쿼리 파라미터 포함)
            "/api/business/*/**",                                      // GET 업체 정보 조회 (SecurityConfig에서 메서드 구분)

            // 예약 슬롯
            "/api/business/*/booking-slot",
            "/api/business/*/booking-slot/range",
            "/api/business/*/booking-slot/menu/*",                  // GET 예약 슬롯 조회
            "/api/business/*/booking-slot/upcoming",

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

    /**
     * 필터 실행 메서드
     * 모든 HTTP 요청에 대해 JWT 인증을 수행합니다.
     * 처리 흐름:
     * 1. 요청 URI 추출
     * 2. 공개 API 여부 확인 (requiresAuthentication)
     * 3. 인증 필요 시: JWT 토큰 검증 및 userId 추출 (validateTokenAndSetUser)
     * 4. 다음 필터로 요청 전달 (filterChain.doFilter)
     * 5. 인증 실패 시: 401 응답 반환 (handleAuthenticationError)
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 다음 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
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
     * AntPathMatcher를 사용하여 PUBLIC_PATHS와 요청 URI를 패턴 매칭합니다.
     * 판단 로직:
     * 1. PUBLIC_PATHS 목록과 패턴 매칭 → 공개 API면 인증 불필요 (false 반환)
     * 2. /api/로 시작하는 경로 → 인증 필요 (true 반환)
     * 3. 그 외 경로 → 인증 불필요 (false 반환)
     *
     * 참고: GET 메서드만 공개인 경로는 SecurityConfig에서 최종 판단
     *      예: /api/business/{businessId}는 GET만 공개, PUT/DELETE는 인증 필요
     *
     * @param requestURI 요청 URI
     * @return 인증 필요 여부 (true: 인증 필요, false: 인증 불필요)
     */
    private boolean requiresAuthentication(String requestURI) {
        // 공개 API인지 확인 (AntPathMatcher 사용)
        for (String publicPath : PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, requestURI)) {
                log.debug("공개 API 접근: {}", requestURI);
                return false;
            }
        }

        // /api/로 시작하는 경로는 인증 필요
        boolean requiresAuth = requestURI.startsWith("/api/");
        if (requiresAuth) {
            log.debug("인증 필요 API: {}", requestURI);
        }
        return requiresAuth;
    }

    /**
     * JWT 토큰 검증 및 사용자 정보 설정
     * Authorization 헤더에서 JWT 토큰을 추출하고 검증한 후, userId를 요청에 저장합니다.
     * 처리 흐름:
     * 1. Authorization 헤더에서 Bearer 토큰 추출 (extractToken)
     * 2. 토큰 존재 여부 확인
     * 3. TokenValidator로 토큰 유효성 검증
     * 4. 토큰에서 userId 추출
     * 5. request.setAttribute("userId", userId) 저장 (Controller에서 @CurrentUserId로 사용)
     * 6. request.setAttribute("token", token) 저장 (필요 시 재사용)
     * 7. SecurityContext에 Authentication 설정 (Spring Security 인증 통과)
     *
     * @param request HTTP 요청
     * @throws AuthException 토큰이 없거나 유효하지 않을 경우 (TOKEN_INVALID)
     */
    private void validateTokenAndSetUser(HttpServletRequest request) {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // TokenValidator를 사용한 토큰 검증
        if (!tokenValidator.isValidToken(token)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 토큰에서 사용자 ID 추출하여 요청에 설정
        UUID userId = tokenValidator.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("token", token);

        // Spring Security 인증 설정
        // SecurityContext에 Authentication 객체를 설정해야 .authenticated() 체크를 통과할 수 있음
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        // principal
                        null,
                        // credentials
                        Collections.emptyList()
                        // authorities
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("JWT 인증 성공: userId={}", userId);
    }

    /**
     * 요청에서 JWT 토큰 추출
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 순수 토큰만 추출합니다.
     * 예시:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
     * → "eyJhbGciOiJIUzI1NiJ9..." 반환
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
     * 응답 형식 예시:
     * {
     *   "success": false,
     *   "message": "인증에 실패했습니다",
     *   "error": {
     *     "code": "TOKEN_INVALID",
     *     "message": "유효하지 않은 토큰입니다"
     *   }
     * }
     *
     * @param response HTTP 응답
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