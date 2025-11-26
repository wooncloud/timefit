package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.util.AuthTokenHelper;
import timefit.auth.service.validator.AuthValidator;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

// 사용자 등록 전담 서비스
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final AuthTokenHelper authTokenHelper;
    private final PasswordEncoder passwordEncoder;

    // 사용자 등록 (User 생성 + 토큰 발급)
    @Transactional
    public AuthResponseDto.UserSignUp registerUser(AuthRequestDto.UserSignUp request) {

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
        AuthTokenHelper.TokenPair tokenPair = authTokenHelper.generateTokenPair(savedUser.getId());

        // 4. DTO 변환
        return AuthResponseDto.UserSignUp.of(
                savedUser,
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken()
        );
    }
}