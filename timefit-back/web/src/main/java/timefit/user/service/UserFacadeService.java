package timefit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import timefit.user.dto.response.CurrentUserResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFacadeService {

    private final UserQueryService userQueryService;

    // 현재 사용자 정보 조회
    public CurrentUserResponse getCurrentUserInfo(UUID userId) {
        return userQueryService.getCurrentUserInfo(userId);
    }
}