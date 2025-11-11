package timefit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.user.dto.response.CurrentUserResponse;
import timefit.user.service.UserFacadeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
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
    @GetMapping("/me")
    public ResponseEntity<ResponseData<CurrentUserResponse>> getCurrentUser(
            @CurrentUserId UUID userId) {

        log.info("현재 사용자 정보 조회 요청: userId={}", userId);

        CurrentUserResponse response = userFacadeService.getCurrentUserInfo(userId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}