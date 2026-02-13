package timefit.auth.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.dto.AuthResponseDto;
import timefit.business.entity.UserBusinessRole;

import java.util.List;

/**
 * Auth 응답 DTO 조립 헬퍼
 * 역할:
 * - Entity List → DTO List 변환
 * - 응답 DTO 조립 로직 집중
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthResponseHelper {

    /**
     * UserBusinessRole List → BusinessInfo DTO List 변환
     *
     * @param userBusinessRoles 사용자 비즈니스 권한 목록
     * @return BusinessInfo DTO 목록
     */
    public List<AuthResponseDto.BusinessInfo> convertToBusinessInfoList(
            List<UserBusinessRole> userBusinessRoles) {

        return userBusinessRoles.stream()
                .map(role -> AuthResponseDto.BusinessInfo.of(
                        role.getBusiness(),
                        role
                ))
                .toList();
    }
}