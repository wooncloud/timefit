package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.factory.UserFactory;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRegistrationService {

    private final UserRepository userRepository;

    /**
     * 사용자 등록 (User만 생성 (role: USER))
     */
    @Transactional
    public UserRegistrationResult registerUser(AuthRequestDto.UserSignUp request) {

        // 1. 중복 체크
        validateEmailDuplication(request);

        // 2. User 생성 (role: USER)
        User user = UserFactory.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getPhoneNumber()
        );
        User savedUser = userRepository.save(user);

        log.info("순수 사용자 등록 완료: userId={}, role={}", savedUser.getId(), savedUser.getRole());

        return UserRegistrationResult.of(savedUser);
    }

    /**
     * 중복 검증
     */
    private void validateEmailDuplication(AuthRequestDto.UserSignUp request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}