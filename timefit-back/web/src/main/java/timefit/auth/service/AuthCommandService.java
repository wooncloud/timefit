package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.dto.OAuthUserInfo;
import timefit.auth.service.dto.TokenPair;
import timefit.auth.service.helper.AuthResponseHelper;
import timefit.auth.service.helper.JwtTokenHelper;
import timefit.auth.service.helper.OAuthHelper;
import timefit.auth.service.validator.AuthValidator;
import timefit.auth.service.validator.OAuthValidator;
import timefit.auth.service.validator.TokenValidator;
import timefit.business.entity.UserBusinessRole;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.entity.RefreshToken;
import timefit.user.entity.User;
import timefit.user.repository.RefreshTokenRepository;
import timefit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Auth 도메인 Command 통합 서비스
 *
 * 역할:
 * - 모든 인증/인가 Command 처리
 * - 회원가입, 로그인, 토큰 갱신, 로그아웃
 *
 * 책임:
 * - 비즈니스 로직 오케스트레이션
 * - 트랜잭션 경계 관리
 * - Helper/Validator에 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCommandService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthValidator authValidator;
    private final TokenValidator tokenValidator;
    private final OAuthValidator oauthValidator;

    private final AuthResponseHelper authResponseHelper;
    private final OAuthHelper oauthHelper;
    private final JwtTokenHelper jwtTokenHelper;

    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 등록 (회원가입)
     *
     * @param request 회원가입 요청 DTO
     * @return 회원가입 응답 DTO (사용자 정보 + 토큰)
     */
    @Transactional
    public AuthResponseDto.UserSignUp registerUser(AuthRequestDto.UserSignUp request) {
        log.info("사용자 등록 처리 시작: email={}", request.email());

        // 1. 중복 체크 & 비밀번호 암호화
        authValidator.validateEmailNotDuplicated(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());

        // 2. User 생성 (Entity 정적 팩토리)
        User user = User.createUser(
                request.email(),
                encodedPassword,
                request.name(),
                request.phoneNumber()
        );

        User savedUser = userRepository.save(user);

        // 3. 토큰 생성
        String jti = UUID.randomUUID().toString();
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(savedUser.getId(), jti);

        // 4. Refresh Token DB 저장
        saveRefreshToken(jti, savedUser.getId());

        log.info("사용자 등록 완료: userId={}, email={}, jti={}",
                savedUser.getId(), savedUser.getEmail(), jti);

        // 5. DTO 반환
        return AuthResponseDto.UserSignUp.of(
                savedUser,
                tokenPair.accessToken(),
                tokenPair.refreshToken()
        );
    }

    /**
     * 일반 로그인 (이메일 + 비밀번호)
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO (사용자 정보 + 비즈니스 목록 + 토큰)
     */
    @Transactional
    public AuthResponseDto.UserSignIn loginUser(AuthRequestDto.UserSignIn request) {
        log.info("사용자 로그인 처리 시작: email={}", request.email());

        // 1. 사용자 조회 및 검증
        User user = authValidator.validateUserCredentials(
                request.email(),
                request.password()
        );

        // 2. 마지막 로그인 시간 업데이트
        user.updateLastLogin();
        userRepository.save(user);

        // 3. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = authValidator.getUserBusinessRoles(user.getId());

        // 4. 토큰 생성
        String jti = UUID.randomUUID().toString();
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(user.getId(), jti);

        // 5. Refresh Token DB 저장
        saveRefreshToken(jti, user.getId());

        // 6. Entity → DTO 변환
        List<AuthResponseDto.BusinessInfo> businessInfos =
                authResponseHelper.convertToBusinessInfoList(userBusinessRoles);

        log.info("사용자 로그인 완료: userId={}, businessCount={}, jti={}",
                user.getId(), userBusinessRoles.size(), jti);

        return AuthResponseDto.UserSignIn.of(
                user,
                businessInfos,
                tokenPair.accessToken(),
                tokenPair.refreshToken()
        );
    }

    /**
     * OAuth 로그인 (Google, Kakao 등)
     *
     * @param request OAuth 로그인 요청 DTO
     * @return OAuth 로그인 응답 DTO (사용자 정보 + 비즈니스 목록 + 토큰 + 최초 로그인 여부)
     */
    @Transactional
    public AuthResponseDto.CustomerOAuth loginOAuthUser(AuthRequestDto.CustomerOAuth request) {
        log.info("OAuth 로그인 처리 시작: provider={}", request.provider());

        // 1. OAuth 토큰 검증 (OAuthValidator에 위임)
        OAuthUserInfo oauthUserInfo = oauthValidator.validateToken(request);

        // 2. 기존 사용자 확인 또는 신규 생성
        User user = oauthHelper.findOrCreateOAuthUser(request, oauthUserInfo);

        boolean isFirstLogin = user.getCreatedAt().equals(user.getLastLoginAt());

        // 3. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = authValidator.getUserBusinessRoles(user.getId());

        // 4. 토큰 생성
        String jti = UUID.randomUUID().toString();
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(user.getId(), jti);

        // 5. Refresh Token DB 저장
        saveRefreshToken(jti, user.getId());

        // 6. Entity → DTO 변환
        List<AuthResponseDto.BusinessInfo> businessInfos =
                authResponseHelper.convertToBusinessInfoList(userBusinessRoles);

        log.info("OAuth 로그인 완료: userId={}, isFirstLogin={}, jti={}",
                user.getId(), isFirstLogin, jti);

        return AuthResponseDto.CustomerOAuth.of(
                user,
                businessInfos,
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                isFirstLogin
        );
    }

    /**
     * 토큰 갱신 (Refresh Token으로 새 Access + Refresh Token 발급)
     *
     * @param request Refresh Token 요청 DTO
     * @return 새로운 Access Token과 Refresh Token
     */
    @Transactional
    public AuthResponseDto.TokenRefresh refreshToken(AuthRequestDto.TokenRefresh request) {
        log.info("토큰 갱신 처리 시작");

        // 1. Refresh Token 유효성 검증
        if (!tokenValidator.isValidRefreshToken(request.refreshToken())) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 2. JWT에서 jti와 userId 추출
        String jti = tokenValidator.getJtiFromRefreshToken(request.refreshToken());
        UUID userId = tokenValidator.getUserIdFromRefreshToken(request.refreshToken());

        // 3. DB에서 Refresh Token 조회
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> {
                    log.warn("DB에 존재하지 않는 Refresh Token: jti={}", jti);
                    return new AuthException(AuthErrorCode.TOKEN_INVALID);
                });

        // 4. 재사용 감지 (이미 무효화된 토큰)
        if (refreshToken.getIsRevoked()) {
            log.error("🚨 Refresh Token 재사용 감지: jti={}, userId={}", jti, userId);

            // 보안 조치: 해당 사용자의 모든 토큰 무효화
            int revokedCount = refreshTokenRepository.revokeAllByUserId(userId);
            log.error("🚨 보안 조치: 사용자의 모든 토큰 무효화 완료 - userId={}, count={}",
                    userId, revokedCount);

            throw new AuthException(AuthErrorCode.TOKEN_REUSED);
        }

        // 5. 토큰 만료 확인
        if (refreshToken.isExpired()) {
            log.warn("만료된 Refresh Token: jti={}, expiresAt={}", jti, refreshToken.getExpiresAt());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }

        // 6. 기존 토큰 무효화 (Refresh Token Rotation)
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        log.debug("기존 Refresh Token 무효화: jti={}", jti);

        // 7. 새 토큰 생성
        String newJti = UUID.randomUUID().toString();
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(userId, newJti);

        // 8. 새 Refresh Token DB 저장
        saveRefreshToken(newJti, userId);

        // 9. 만료 시간 계산
        Date expirationDate = tokenValidator.getExpirationDate(tokenPair.accessToken());
        long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

        log.info("토큰 갱신 완료: userId={}, oldJti={}, newJti={}", userId, jti, newJti);

        // 10. DTO 반환
        return AuthResponseDto.TokenRefresh.of(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                "Bearer",
                expiresIn
        );
    }

    /**
     * 로그아웃 (단일 디바이스)
     *
     * @param refreshToken Refresh Token
     */
    @Transactional
    public void logout(String refreshToken) {
        log.info("로그아웃 처리 시작");

        try {
            // 1. Refresh Token 검증 및 jti 추출
            if (!tokenValidator.isValidRefreshToken(refreshToken)) {
                log.warn("유효하지 않은 Refresh Token으로 로그아웃 시도");
                return; // 이미 무효화되었거나 잘못된 토큰
            }

            String jti = tokenValidator.getJtiFromRefreshToken(refreshToken);

            // 2. DB에서 Refresh Token 조회 및 무효화
            refreshTokenRepository.findByJti(jti).ifPresentOrElse(
                    token -> {
                        token.revoke();
                        refreshTokenRepository.save(token);
                        log.info("로그아웃 완료: jti={}, userId={}", jti, token.getUserId());
                    },
                    () -> log.warn("DB에 존재하지 않는 Refresh Token: jti={}", jti)
            );

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류: {}", e.getMessage());
            // 로그아웃은 실패해도 예외를 던지지 않음 (사용자 경험)
        }
    }

    /**
     * 전체 로그아웃 (모든 디바이스)
     * @param userId 사용자 ID
     */
    @Transactional
    public void logoutAll(UUID userId) {
        log.info("전체 로그아웃 처리 시작: userId={}", userId);

        int revokedCount = refreshTokenRepository.revokeAllByUserId(userId);

        log.info("전체 로그아웃 완료: userId={}, revokedCount={}", userId, revokedCount);
    }

    /**
     * Refresh Token DB 저장 (Private Helper)
     *
     * @param jti JWT ID
     * @param userId 사용자 ID
     */
    private void saveRefreshToken(String jti, UUID userId) {
        // 만료 시간 계산 (현재 시간 + Refresh Token 만료 시간)
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtConfig.getRefreshToken().getExpiration() / 1000);

        RefreshToken refreshToken = RefreshToken.of(jti, userId, expiresAt);
        refreshTokenRepository.save(refreshToken);

        log.debug("Refresh Token DB 저장 완료: jti={}, userId={}, expiresAt={}",
                jti, userId, expiresAt);
    }
}