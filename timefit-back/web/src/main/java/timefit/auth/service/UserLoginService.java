package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.dto.OAuthUserInfo;
import timefit.auth.service.util.AuthTokenHelper;
import timefit.auth.service.validator.AuthValidator;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.List;

// 사용자 로그인 전담 서비스
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLoginService {

    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final OAuthValidationService oAuthValidationService;
    private final AuthValidator authValidator;
    private final AuthTokenHelper authTokenHelper;

    // 일반 로그인 (인증 + 토큰 발급)
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
        List<UserBusinessRole> userBusinessRoles = getUserBusinessRoles(user.getId());

        // 4. 토큰 생성
        AuthTokenHelper.TokenPair tokenPair = authTokenHelper.generateTokenPair(user.getId());

        // 5. Entity → DTO 변환 (Service에서 처리)
        List<AuthResponseDto.BusinessInfo> businessInfos = convertToBusinessInfoList(userBusinessRoles);

        log.info("사용자 로그인 완료: userId={}, businessCount={}",
                user.getId(), userBusinessRoles.size());

        return AuthResponseDto.UserSignIn.of(
                user,
                businessInfos,
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken()
        );
    }

    // OAuth 로그인 (인증 + 토큰 발급)
    @Transactional
    public AuthResponseDto.CustomerOAuth loginOAuthUser(AuthRequestDto.CustomerOAuth request) {
        log.info("OAuth 로그인 처리 시작: provider={}", request.provider());

        // 1. OAuth 토큰 검증
        OAuthUserInfo oauthUserInfo = oAuthValidationService.validateToken(request);

        // 2. 기존 사용자 확인 또는 신규 생성
        User user = findOrCreateOAuthUser(request, oauthUserInfo);

        boolean isFirstLogin = user.getCreatedAt().equals(user.getLastLoginAt());

        // 3. 사용자의 비즈니스 권한 조회
        List<UserBusinessRole> userBusinessRoles = getUserBusinessRoles(user.getId());

        // 4. 토큰 생성
        AuthTokenHelper.TokenPair tokenPair = authTokenHelper.generateTokenPair(user.getId());

        // 5. Entity → DTO 변환 (Service에서 처리)
        List<AuthResponseDto.BusinessInfo> businessInfos = convertToBusinessInfoList(userBusinessRoles);

        log.info("OAuth 로그인 완료: userId={}, isFirstLogin={}",
                user.getId(), isFirstLogin);

        return AuthResponseDto.CustomerOAuth.of(
                user,
                businessInfos,
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken(),
                isFirstLogin
        );
    }

    // 사용자의 비즈니스 권한 조회
    private List<UserBusinessRole> getUserBusinessRoles(java.util.UUID userId) {
        return userBusinessRoleRepository.findByUserIdAndIsActive(userId, true);
    }

    // OAuth 사용자 조회 또는 생성
    private User findOrCreateOAuthUser(
            AuthRequestDto.CustomerOAuth request,
            OAuthUserInfo oauthUserInfo) {

        User existingUser = authValidator.findOAuthUser(
                request.provider(),
                request.oauthId()
        );

        if (existingUser != null) {
            existingUser.updateLastLogin();
            return userRepository.save(existingUser);
        } else {
            User newUser = User.createOAuthUser(
                    oauthUserInfo.email(),
                    oauthUserInfo.name(),
                    oauthUserInfo.profileImageUrl(),
                    request.provider(),
                    request.oauthId()
            );
            return userRepository.save(newUser);
        }
    }

    /**
     * Entity List → DTO List 변환 (Service의 책임)
     * DTO가 아닌 Service 에서 변환 로직 처리
     */
    private List<AuthResponseDto.BusinessInfo> convertToBusinessInfoList(
            List<UserBusinessRole> userBusinessRoles) {

        return userBusinessRoles.stream()
                .map(role -> AuthResponseDto.BusinessInfo.of(
                        role.getBusiness(),
                        role
                ))
                .toList();
    }
}