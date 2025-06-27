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
import timefit.auth.filter.AuthFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter temporaryAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안함
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/business/auth/**").permitAll() // 업체 인증 관련 API
                        .requestMatchers("/api/customer/auth/**").permitAll() // 고객 인증 관련 API
                        .requestMatchers("/api/health").permitAll() // 헬스 체크

                        .requestMatchers("/api/auth/logout").permitAll() // 로그아웃
                        .requestMatchers("/api/auth/token/status").permitAll() // ㅌㅋ

                        .requestMatchers("/api/validation/**").permitAll() // 검증 관련


                        .requestMatchers("/actuator/**").permitAll() // Actuator
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // 정적 리소스
                        .requestMatchers("/api/**").authenticated() // 기타 모든 API는 인증 필요
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // temp 인증 필터 추가
                .addFilterBefore(temporaryAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}