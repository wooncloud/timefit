package timefit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import timefit.user.dto.UserRequestDto;
import timefit.user.dto.UserResponseDto;

import java.util.UUID;

/**
 * User Facade Service (통합 진입점)
 *
 * 주요 역할:
 * - Controller와 세부 서비스 간 통합 인터페이스 제공
 * - 트랜잭션 경계 설정
 * - 여러 서비스 계층 간 조율
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserFacadeService {

    private final UserQueryService queryService;
    private final UserCommandService commandService;

    /**
     * 현재 로그인한 사용자 정보 조회 (/api/user/me)
     * - 사용자 기본 정보
     * - 소속 업체 목록 (사업자인 경우)
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 + 업체 목록
     */
    public UserResponseDto.CurrentUser getCurrentUser(UUID userId) {
        return queryService.getCurrentUserInfo(userId);
    }

    /**
     * 사용자 프로필 조회 (/api/customer/profile)
     *
     * @param userId 사용자 ID
     * @return 사용자 프로필 + 통계
     */
    public UserResponseDto.UserProfile getUserProfile(UUID userId) {
        return queryService.getUserProfile(userId);
    }

    /**
     * 프로필 수정
     *
     * @param userId 사용자 ID
     * @param request 프로필 수정 요청
     * @return 수정된 프로필
     */
    public UserResponseDto.UserProfile updateProfile(
            UUID userId,
            UserRequestDto.UpdateProfile request) {

        return commandService.updateProfile(userId, request);
    }

    /**
     * 비밀번호 변경
     *
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청
     */
    public void changePassword(
            UUID userId,
            UserRequestDto.ChangePassword request) {

        commandService.changePassword(userId, request);
    }
}