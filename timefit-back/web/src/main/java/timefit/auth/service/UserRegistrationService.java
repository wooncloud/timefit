package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.util.AuthTokenHelper;
import timefit.auth.service.validator.AuthValidator;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

/**
 * 사용자 등록 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final AuthTokenHelper authTokenHelper;

    /**
     * 사용자 등록 (User 생성 + 토큰 발급)
     *
     * @return 회원가입 응답 DTO
     */
    @Transactional
    public AuthResponseDto.UserSignUp registerUser(AuthRequestDto.UserSignUp request) {

        // 1. 중복 체크 (Validator 사용)
        authValidator.validateEmailNotDuplicated(request.getEmail());

        // 2. User 생성 (Entity 정적 팩토리)
        User user = User.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getPhoneNumber()
        );

        User savedUser = userRepository.save(user);

        // 3. 토큰 생성 (Helper 사용)
        AuthTokenHelper.TokenPair tokenPair = authTokenHelper.generateTokenPair(savedUser.getId());

        // 4. DTO 변환 및 반환
        log.info("사용자 등록 완료: userId={}, role={}", savedUser.getId(), savedUser.getRole());

        return AuthResponseDto.UserSignUp.of(
                savedUser,
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken()
        );
    }
}