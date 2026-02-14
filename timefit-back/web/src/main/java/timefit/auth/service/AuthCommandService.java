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
import timefit.auth.service.helper.OAuthHelper;
import timefit.auth.service.validator.AuthValidator;
import timefit.auth.service.validator.OAuthValidator;
import timefit.auth.service.validator.TokenValidator;
import timefit.auth.service.helper.JwtTokenHelper;
import timefit.business.entity.UserBusinessRole;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

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
 *
 * 의존성:
 * - Repository: UserRepository
 * - Validator: AuthValidator, TokenValidator, OAuthValidator
 * - Helper: AuthTokenHelper, AuthResponseHelper, OAuthUserHelper
 * - Util: JwtTokenUtil
 * - External: PasswordEncoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthCommandService {

    private final UserRepository userRepository;

    private final AuthValidator authValidator;
    private final TokenValidator tokenValidator;
    private final OAuthValidator oauthValidator;

    private final AuthResponseHelper authResponseHelper;
    private final OAuthHelper oauthHelper;
    private final JwtTokenHelper jwtTokenHelper;
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

        log.info("사용자 등록 완료: userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        // 3. 토큰 생성
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(savedUser.getId());

        // 4. DTO 반환
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
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(user.getId());

        // 5. Entity → DTO 변환
        List<AuthResponseDto.BusinessInfo> businessInfos =
                authResponseHelper.convertToBusinessInfoList(userBusinessRoles);

        log.info("사용자 로그인 완료: userId={}, businessCount={}",
                user.getId(), userBusinessRoles.size());

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
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(user.getId());

        // 5. Entity → DTO 변환
        List<AuthResponseDto.BusinessInfo> businessInfos =
                authResponseHelper.convertToBusinessInfoList(userBusinessRoles);

        log.info("OAuth 로그인 완료: userId={}, isFirstLogin={}",
                user.getId(), isFirstLogin);

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

        // 유효 토큰 인지 검증
        if (!tokenValidator.isValidRefreshToken(request.refreshToken())) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // Refresh 토큰 으로 부터 사용자 검증
        UUID userId = tokenValidator.getUserIdFromRefreshToken(request.refreshToken());

        // 3. 새 토큰 생성
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(userId);

        // 4. 만료 시간 계산
        Date expirationDate = tokenValidator.getExpirationDate(tokenPair.accessToken());
        long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

        log.info("토큰 갱신 완료: userId={}", userId);

        // 5. DTO 반환
        return AuthResponseDto.TokenRefresh.of(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                "Bearer",
                expiresIn
        );
    }

    /**
     * 토큰 무효화 (로그아웃)
     *
     * @param token JWT 토큰
     */
    @Transactional
    public void invalidateToken(String token) {
        log.info("토큰 무효화 요청: {}", token.substring(0, Math.min(20, token.length())));
        // TODO: 실제 무효화 로직 구현 (Redis Blacklist 등)
    }
}