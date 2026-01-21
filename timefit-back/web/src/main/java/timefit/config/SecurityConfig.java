package timefit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import timefit.auth.filter.JwtAuthFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정
 *
 * 애플리케이션의 보안 정책을 정의하는 설정 클래스입니다.
 * 경로별 접근 권한, CORS 정책, JWT 인증 필터 체인, 비밀번호 암호화 방식 등을 설정합니다.
 *
 * 적용 시점:
 * 애플리케이션 시작 시 빈으로 등록되어 모든 HTTP 요청에 적용됩니다.
 * HTTP 요청 → JwtAuthFilter → [SecurityFilterChain] → Controller
 *
 * 주요 기능:
 * - 경로별 접근 권한 설정 (공개 API vs 인증 필요 API)
 * - CORS 정책 설정 (프론트엔드와의 통신 허용)
 * - JWT 인증 필터 체인 구성
 * - 비밀번호 암호화 알고리즘(BCrypt) 설정
 *
 * 책임 범위:
 * - 경로별 접근 권한 설정 (permitAll / authenticated)
 * - CORS 정책 설정
 * - JWT 인증 필터 체인 구성
 * - 비밀번호 암호화 방식 설정
 *
 * 동작 순서:
 * 1. HTTP 요청 수신
 * 2. JwtAuthFilter 실행 (JWT 토큰 검증 및 사용자 정보 추출)
 * 3. SecurityFilterChain 실행 (경로별 접근 권한 확인)
 * 4. Controller로 요청 전달
 *
 * JwtAuthFilter와의 관계:
 * - JwtAuthFilter를 SecurityFilterChain에 등록하여 JWT 인증 처리
 * - JwtAuthFilter의 PUBLIC_PATHS와 이 클래스의 permitAll() 설정이 일치해야 함
 * - 두 설정이 불일치하면 인증 오류 발생 가능
 *
 * 주의사항:
 * - requestMatchers() 순서가 중요 (구체적 → 포괄적 순서)
 * - permitAll()과 authenticated()의 순서를 잘못 설정하면 의도하지 않은 접근 차단 발생
 * - CORS 설정은 프론트엔드 도메인과 일치해야 함
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 비밀번호 암호화 엔코더
     * BCrypt 알고리즘 사용 (strength 10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 필터 체인 설정
     *
     * 설정 항목:
     * 1. CORS: 개발 환경 모든 origin 허용
     * 2. CSRF: REST API이므로 비활성화
     * 3. Session: Stateless (JWT 사용)
     * 4. 경로별 권한: 공개 API와 인증 필요 API 구분
     * 5. Custom Filter: JwtAuthFilter 추가
     *
     * @param http HttpSecurity 설정 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 중 발생하는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CORS 설정 (개발 환경)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화 (REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용하지 않음 (JWT Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // ========== 인증 관련 API ==========
                        .requestMatchers("/api/auth/signup", "/api/auth/signin",
                                "/api/auth/refresh", "/api/auth/oauth",
                                "/api/auth/health").permitAll()
                        .requestMatchers("/api/auth/**").authenticated()   // 나머지는 인증 필요 (logout, refresh 등)

                        // ========== 업체 관련 공개 API ==========
                        .requestMatchers("/api/business/search", "/api/business/search/**").permitAll()  // 업체 검색 (쿼리 파라미터 포함) |  ** 와일드 카드를 통해 ?name=헤어샵&page=1 같은 쿼리 지원
                        .requestMatchers(HttpMethod.GET, "/api/business/*").permitAll()                      // 업체 상세
                        .requestMatchers(HttpMethod.GET, "/api/business/*/operating-hours").permitAll()      // 영업시간
                        .requestMatchers(HttpMethod.GET, "/api/business/*/menu").permitAll()                 // 메뉴 목록
                        .requestMatchers(HttpMethod.GET, "/api/business/*/menu/*").permitAll()

                        // ========== 예약 슬롯 조회 (공개) ==========
                        .requestMatchers(HttpMethod.GET,
                                "/api/business/*/booking-slot",
                                "/api/business/*/booking-slot/range",
                                "/api/business/*/booking-slot/menu/*",
                                "/api/business/*/booking-slot/upcoming",
                                "/api/business/*/booking-slot/**"
                        ).permitAll()

                        // ========== 업체 관련 인증 필요 API ==========
                        .requestMatchers("/api/business/**").authenticated()  // 나머지 업체 API는 인증 필요

                        // ========== 예약 관련 API (인증 필요) ==========
                        .requestMatchers("/api/reservation/**").authenticated()
                        .requestMatchers("/api/reservations/**").authenticated()

                        // ========== 초대 관련 API (인증 필요) ==========
                        .requestMatchers("/api/invitation/**").authenticated()

                        // ========== 사용자 API (인증 필요) ==========
                        .requestMatchers("/api/user/**").authenticated()

                        // ========== 검증 API (공개) ==========
                        .requestMatchers("/api/validation/**").permitAll()

                        // ========== 테스트 API (개발용) ==========
                        .requestMatchers("/api/test/**").permitAll()

                        // ========== 개발/모니터링 도구 ==========
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v1/api-docs/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // ========== 기타 ==========
                        .requestMatchers("/api/**").authenticated()  // 나머지 모든 /api/** 는 인증 필요
                        .anyRequest().permitAll()  // 그 외 요청 허용
                )

                // JWT 인증 필터 추가
                // UsernamePasswordAuthenticationFilter 앞에 배치하여
                // Spring Security가 인증을 처리하기 전에 JWT 토큰을 먼저 검증
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * CORS 설정
     * 차후 setAllowedOriginPatterns / setAllowedOrigins profile 별도 생성
     * Note:
     *    - setAllowedOriginPatterns(List.of("*")) ← 모든 origin 허용 (보안 취약)
     *    - setAllowedOrigins(List.of("*")) ← 작동하지 않음 (Spring Security 제약)
     *    와일드카드 패턴이 필요한 경우:
     *    - setAllowedOriginPatterns 사용 (단, 매우 제한적으로)
     *    - 예: 서브도메인이 동적으로 생성되는 경우
     *      configuration.setAllowedOriginPatterns(List.of("https://*.timefit.com"));
     *    - 주의: 프로덕션에서는 가급적 피하고, 명시적 origin 나열 권장
     * @return CORS 설정 객체
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 개발 환경: localhost 포트 명시
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",     // React 기본 포트
                "http://localhost:5173",     // Vite 기본 포트
                "http://localhost:8080"      // 백엔드 포트 (Swagger 테스트용)
        ));

        // ========== 허용할 HTTP 메서드 ==========
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // ========== 허용할 헤더 ==========
        configuration.setAllowedHeaders(List.of("*"));

        // ========== 인증 정보 포함 허용 (쿠키 등) ==========
        // setAllowedOrigins 사용 시 반드시 true로 설정해야 함
        configuration.setAllowCredentials(true);

        // ========== 노출할 헤더 (프론트엔드에서 접근 가능한 헤더) ==========
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"  // 페이지네이션용 (필요 시)
        ));

        // ========== Preflight 요청 캐시 시간 (초) ==========
        // OPTIONS 요청을 매번 보내지 않도록 캐시
        configuration.setMaxAge(3600L);  // 1시간

        // ========== 모든 경로에 CORS 설정 적용 ==========
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}