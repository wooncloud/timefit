package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.dto.OAuthUserInfo;
import timefit.auth.service.dto.RefreshTokenClaims;
import timefit.auth.service.dto.TokenPair;
import timefit.auth.service.helper.*;
import timefit.auth.service.validator.AuthValidator;
import timefit.auth.service.validator.OAuthValidator;
import timefit.auth.service.validator.RefreshTokenValidator;
import timefit.auth.service.validator.TokenValidator;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.RefreshToken;
import timefit.user.entity.User;

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

    private final OAuthValidator oauthValidator;
    private final AuthValidator authValidator;
    private final TokenValidator tokenValidator;
    private final RefreshTokenValidator refreshTokenValidator;

    private final OAuthHelper oauthHelper;
    private final AuthResponseHelper authResponseHelper;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final RefreshTokenHelper refreshTokenHelper;
    private final UserRegistrationHelper userRegistrationHelper;


    /**
     * 사용자 등록 (회원가입)
     *
     * @param request 회원가입 요청 DTO
     * @return 회원가입 응답 DTO (사용자 정보 + 토큰)
     */
    @Transactional
    public AuthResponseDto.UserSignUp registerUser(AuthRequestDto.UserSignUp request) {
        log.info("사용자 등록 처리 시작: email={}", request.email());

        // 1. 중복 체크
        authValidator.validateEmailNotDuplicated(request.email());

        // 2. 신규 유저 생성 (비밀번호 암호화 + DB 저장)
        User savedUser = userRegistrationHelper.registerNewUser(request);

        // 3. 토큰 발급 (생성 + DB 저장)
        TokenPair tokenPair = refreshTokenIssuer.generateAndSaveTokenPair(savedUser.getId());

        log.info("사용자 등록 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

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
        User user = authValidator.validateUserCredentials(request.email(), request.password());

        // 2. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = authValidator.getUserBusinessRoles(user.getId());

        // 3. 토큰 발급 (생성 + DB 저장)
        TokenPair tokenPair = refreshTokenIssuer.generateAndSaveTokenPair(user.getId());

        // 4. Entity → DTO 변환 (효율적)
        List<AuthResponseDto.BusinessInfo> businessInfos =
                authResponseHelper.convertToBusinessInfoList(userBusinessRoles);

        // 5. 마지막 로그인 시간 업데이트
        user.updateLastLogin();

        log.info("사용자 로그인 완료: userId={}, businessCount={}", user.getId(), userBusinessRoles.size());

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

        // 1. OAuth 토큰 검증
        OAuthUserInfo oauthUserInfo = oauthValidator.validateToken(request);

        // 2. 기존 사용자 확인 또는 신규 생성
        User user = oauthHelper.findOrCreateOAuthUser(request, oauthUserInfo);

        boolean isFirstLogin = user.getCreatedAt().equals(user.getLastLoginAt());

        // 3. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = authValidator.getUserBusinessRoles(user.getId());

        // 4. 토큰 발급 (생성 + DB 저장)
        TokenPair tokenPair = refreshTokenIssuer.generateAndSaveTokenPair(user.getId());

        // 5. Entity → DTO 변환 (효율적)
        List<AuthResponseDto.BusinessInfo> businessInfos =
                authResponseHelper.convertToBusinessInfoList(userBusinessRoles);

        log.info("OAuth 로그인 완료: userId={}, isFirstLogin={}", user.getId(), isFirstLogin);

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

        // 1. Refresh Token에서 jti와 userId 추출 (1번만 검증!)
        RefreshTokenClaims claims = tokenValidator.extractRefreshTokenClaims(request.refreshToken());

        // 2. 기존 토큰 유효성 검증 후 무효화
        String jti = claims.jti();
        UUID userId = claims.userId();
        RefreshToken refreshToken = refreshTokenValidator.validateJtiExists(jti);

        refreshTokenValidator.validateForRotation(refreshToken, userId);
        refreshToken.revoke();
        log.debug("기존 Refresh Token 무효화: jti={}", jti);

        // 3. 신규 TokenPair 생성
        TokenPair tokenPair = refreshTokenIssuer.generateAndSaveTokenPair(userId);
        Date expirationDate = tokenValidator.getExpirationDate(tokenPair.accessToken());
        long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

        log.info("토큰 갱신 완료: userId={}, oldJti={}", userId, jti);

        return AuthResponseDto.TokenRefresh.of(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                "Bearer",
                expiresIn
        );
    }

    /**
     * 로그아웃 (단일 디바이스 Refresh Token 무효화)
     *
     * 현재 구현:
     * - Refresh Token만 DB에서 무효화 (즉시 차단)
     * - Access Token은 만료 시간(15분)까지 유효 (JWT Stateless 특성)
     *
     * 설계 결정:
     * - JWT Stateless 원칙 < 보안 우선
     * - Access Token: Stateless 유지 (빠른 검증)
     * - Refresh Token: DB 저장 (로그아웃/재사용 감지 가능)
     *
     * 주의사항:
     * - 완전한 즉시 차단 불가능 (Access Token 만료 대기)
     * - 민감한 작업은 재인증 필요
     *
     * @param refreshToken Refresh Token (JWT)
     * @param userId 로그아웃 요청 사용자 ID
     */
    @Transactional
    public void logout(String refreshToken, UUID userId) {
        log.info("로그아웃 처리 시작: userId={}", userId);

        // 1. Refresh Token 검증 및 Claims 추출
        RefreshTokenClaims claims = tokenValidator.extractRefreshTokenClaims(refreshToken);

        // 2. RefreshToken 무효화
        refreshTokenHelper.revokeByJti(claims.jti());

        log.info("로그아웃 완료: userId={}", userId);
    }

}