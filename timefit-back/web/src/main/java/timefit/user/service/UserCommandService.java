package timefit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.validator.AuthValidator;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.dto.UserRequestDto;
import timefit.user.dto.UserResponseDto;
import timefit.user.entity.User;

import java.util.UUID;

/**
 * User 데이터 변경 전담 서비스 (UD)
 *
 * 주요 역할:
 * - 프로필 수정 (이름, 전화번호, 프로필 이미지)
 * - 비밀번호 변경
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserQueryService queryService;

    /**
     * 프로필 수정
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 프로필 업데이트 (null이 아닌 필드만)
     * 3. 통계와 함께 응답
     *
     * @param userId 사용자 ID
     * @param request 프로필 수정 요청
     * @return 수정된 프로필
     */
    public UserResponseDto.UserProfile updateProfile(
            UUID userId,
            UserRequestDto.UpdateProfile request) {

        log.info("프로필 수정 시작: userId={}", userId);

        // 1. 사용자 검증
        User user = authValidator.validateUserExists(userId);

        // 2. 프로필 업데이트 (null이 아닌 필드만)
        if (request.name() != null) {
            user.updateName(request.name());
        }

        if (request.phoneNumber() != null) {
            user.updatePhoneNumber(request.phoneNumber());
        }

        if (request.profileImageUrl() != null) {
            user.updateProfileImageUrl(request.profileImageUrl());
        }

        log.info("프로필 수정 완료: userId={}", userId);

        // 3. 통계와 함께 응답
        UserResponseDto.UserStatistics statistics = queryService.getUserStatistics(userId);
        return UserResponseDto.UserProfile.of(user, statistics);
    }

    /**
     * 비밀번호 변경
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 현재 비밀번호 확인
     * 3. 새 비밀번호 확인 일치 검증
     * 4. 비밀번호 암호화 및 변경
     *
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청
     */
    public void changePassword(
            UUID userId,
            UserRequestDto.ChangePassword request) {

        log.info("비밀번호 변경 시작: userId={}", userId);

        // 1. 사용자 검증
        User user = authValidator.validateUserExists(userId);

        // 2. 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            log.warn("현재 비밀번호 불일치: userId={}", userId);
            throw new AuthException(AuthErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 3. 새 비밀번호 확인 일치 검증
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            log.warn("새 비밀번호 확인 불일치: userId={}", userId);
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        // 4. 비밀번호 암호화 및 변경
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedPassword);

        log.info("비밀번호 변경 완료: userId={}", userId);
    }
}