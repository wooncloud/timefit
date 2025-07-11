package timefit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import timefit.auth.filter.AuthFilter;

import java.util.Arrays;

/**
 * 역할: 경로별 접근 권한 설정, AuthFilter에 토큰 검증 위임
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (API 서버)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 사용 안함 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 접근 권한 설정 (AuthFilter에 실제 검증 위임)
                .authorizeHttpRequests(auth -> auth
                        // 공개 API - 인증 불필요
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/validation/**").permitAll()

                        // AuthFilter가 토큰 검증할 API - permitAll (실제 검증은 AuthFilter)
                        .requestMatchers("/api/business/**").permitAll()
                        .requestMatchers("/api/reservations/**").permitAll()
                        .requestMatchers("/api/services/**").permitAll()
                        .requestMatchers("/api/customers/**").permitAll()

                        // 시스템 API
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // 나머지 요청은 인증 필요 (실제로는 AuthFilter 에서 처리)
                        .anyRequest().authenticated()
                )

                // AuthFilter를 UsernamePasswordAuthenticationFilter 이전에 추가
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 개발 환경에서는 모든 도메인 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        // 토큰 헤더 노출
        configuration.setExposedHeaders(Arrays.asList("x-client-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * 비밀번호 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}