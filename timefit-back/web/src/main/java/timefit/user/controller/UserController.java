package timefit.user.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.user.*;
import timefit.common.swagger.requestbody.user.*;
import timefit.user.dto.UserRequestDto;
import timefit.user.dto.UserResponseDto;
import timefit.user.service.UserFacadeService;

import java.util.UUID;

@Tag(name = "00. 사용자 관리", description = "사용자 정보 조회 및 프로필 관리 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserFacadeService userFacadeService;

    /**
     * 현재 로그인한 사용자 정보 조회
     * - 사용자 기본 정보
     * - 소속 업체 목록 (사업자인 경우)
     * - 일반 고객인 경우 businesses는 빈 배열
     *
     * @param userId 현재 로그인한 사용자 ID
     * @return 사용자 정보 + 업체 목록
     */
    @GetCurrentUserOperation
    @GetMapping("/user/me")
    public ResponseEntity<ResponseData<UserResponseDto.CurrentUser>> getCurrentUser(
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("현재 사용자 정보 조회 요청: userId={}", userId);

        UserResponseDto.CurrentUser response = userFacadeService.getCurrentUser(userId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========== 고객 전용 API ==========

    /**
     * 내 프로필 조회 (통계 포함)
     * GET /api/customer/profile
     */
    @GetUserProfileOperation
    @GetMapping("/customer/profile")
    public ResponseEntity<ResponseData<UserResponseDto.UserProfile>> getUserProfile(
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("프로필 조회 요청: userId={}", userId);

        UserResponseDto.UserProfile response = userFacadeService.getUserProfile(userId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 프로필 수정
     * PUT /api/customer/profile
     */
    @UpdateProfileOperation
    @PutMapping("/customer/profile")
    public ResponseEntity<ResponseData<UserResponseDto.UserProfile>> updateProfile(
            @UpdateProfileBody
            @Valid @RequestBody UserRequestDto.UpdateProfile request,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("프로필 수정 요청: userId={}", userId);

        UserResponseDto.UserProfile response =
                userFacadeService.updateProfile(userId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 비밀번호 변경
     * PUT /api/customer/profile/password
     */
    @ChangePasswordOperation
    @PutMapping("/customer/profile/password")
    public ResponseEntity<ResponseData<Void>> changePassword(
            @ChangePasswordBody
            @Valid @RequestBody UserRequestDto.ChangePassword request,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("비밀번호 변경 요청: userId={}", userId);

        userFacadeService.changePassword(userId, request);

        return ResponseEntity.ok(ResponseData.of(null));
    }
}