package timefit.operatinghours.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessHours;
import timefit.business.entity.OperatingHours;
import timefit.business.repository.BusinessHoursRepository;
import timefit.business.repository.OperatingHoursRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.common.entity.DayOfWeek;
import timefit.operatinghours.dto.OperatingHoursRequestDto;
import timefit.operatinghours.dto.OperatingHoursResponseDto;
import timefit.operatinghours.service.helper.BusinessHoursHelper;
import timefit.operatinghours.service.helper.OperatingHoursHelper;
import timefit.operatinghours.service.util.OperatingHoursResponseGenerator;
import timefit.operatinghours.service.validator.OperatingHoursValidator;

import java.util.List;
import java.util.UUID;

/**
 * OperatingHours Command Service
 *
 * 리팩토링 완료:
 * - Private 메서드 3개 제거 (150줄 감소)
 * - Helper/Validator 패턴 적용
 * - OH-05-v2 미래 예약 보호 구현
 * - Menu 모듈 스타일 적용
 *
 * Before: 420줄
 * After: 150줄 (-64%)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperatingHoursCommandService {

    // Repository
    private final BusinessHoursRepository businessHoursRepository;
    private final OperatingHoursRepository operatingHoursRepository;

    // Validator
    private final BusinessValidator businessValidator;
    private final OperatingHoursValidator validator;

    // Helper
    private final BusinessHoursHelper businessHoursHelper;
    private final OperatingHoursHelper operatingHoursHelper;

    // Util
    private final OperatingHoursResponseGenerator responseGenerator;

    /**
     * 영업시간 설정 (BusinessHours + OperatingHours 통합)
     *
     * 1. 권한 검증
     * 2. Request 검증 (Validator)
     * 3. BusinessHours 업데이트 (Helper)
     * 4. OperatingHours 재생성 (Helper + OH-05-v2 검증)
     * 5. Response 생성
     *
     * @param businessId 업체 ID
     * @param request 영업시간 설정 요청
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 설정 결과
     */
    public OperatingHoursResponseDto.OperatingHours setOperatingHours(
            UUID businessId,
            OperatingHoursRequestDto.SetOperatingHours request,
            UUID currentUserId) {

        log.info("영업시간 설정 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessExists(businessId);
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. Request 검증 (BH-01, OH-01)
        validator.validateSetOperatingHoursRequest(request);

        // 3. BusinessHours 업데이트 (Helper)
        List<BusinessHours> updatedBusinessHours =
                businessHoursHelper.updateBusinessHours(business, request);

        // 4. OperatingHours 재생성 (Helper + OH-05-v2)
        List<OperatingHours> newOperatingHours =
                operatingHoursHelper.recreateOperatingHours(business, request);

        log.info("영업시간 설정 완료: businessId={}, businessHours={}, operatingHours={}",
                businessId, updatedBusinessHours.size(), newOperatingHours.size());

        // 5. Response 생성
        return responseGenerator.generateResponse(
                businessId,
                business.getBusinessName(),
                updatedBusinessHours,
                newOperatingHours
        );
    }

    /**
     * 영업시간 리셋 (디폴트 값으로)
     *
     * 1. 권한 검증
     * 2. BusinessHours 디폴트 리셋 (Helper)
     * 3. OperatingHours 삭제
     * 4. Response 생성
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 리셋 결과
     */
    public OperatingHoursResponseDto.OperatingHours resetToDefault(
            UUID businessId,
            UUID currentUserId) {

        log.info("영업시간 리셋 시작: businessId={}", businessId);

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessExists(businessId);
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. BusinessHours 디폴트 리셋 (Helper)
        List<BusinessHours> updatedBusinessHours =
                businessHoursHelper.resetToDefault(business);

        // 3. OperatingHours 삭제 (디폴트는 예약 시간대 없음)
        operatingHoursRepository.deleteByBusinessId(businessId);

        log.info("영업시간 리셋 완료: businessId={}, 디폴트 설정 적용", businessId);

        // 4. Response 생성 (OperatingHours는 빈 리스트)
        return responseGenerator.generateResponse(
                businessId,
                business.getBusinessName(),
                updatedBusinessHours,
                List.of()
        );
    }

    /**
     * 특정 요일 전체 휴무 토글
     *
     * BH-04-v2 정책: 예약이 있어도 토글 허용 (유연한 운영)
     * - BusinessHours와 OperatingHours 모두 토글
     * - 휴무 전환 시: 예약 불가 상태로만 변경 (기존 예약 유지)
     * - 영업 재개 시: 예약 가능 상태로 변경
     *
     * 1. 권한 검증
     * 2. BusinessHours 토글 (Helper)
     * 3. OperatingHours 토글 (Helper)
     * 4. 전체 조회 및 Response
     *
     * @param businessId 업체 ID
     * @param dayOfWeek 요일 (0=일요일 ~ 6=토요일)
     * @param currentUserId 현재 사용자 ID
     * @return 영업시간 조회 결과
     */
    public OperatingHoursResponseDto.OperatingHours toggleBusinessDayOpenStatus(
            UUID businessId,
            Integer dayOfWeek,
            UUID currentUserId) {

        log.info("요일 전체 휴무 토글 시작: businessId={}, dayOfWeek={}",
                businessId, dayOfWeek);

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessExists(businessId);
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        DayOfWeek day = DayOfWeek.fromValue(dayOfWeek);

        // 2. BusinessHours 토글 (Helper)
        businessHoursHelper.toggleBusinessDay(businessId, day);

        // 3. OperatingHours 토글 (Helper)
        operatingHoursHelper.toggleOperatingHoursForDay(businessId, day);

        log.info("요일 전체 휴무 토글 완료: businessId={}, dayOfWeek={}",
                businessId, dayOfWeek);

        // 4. 전체 조회 및 Response
        // QueryService 대신 직접 조회 (트랜잭션 내에서 변경사항 반영 필요)
        List<BusinessHours> businessHours =
                businessHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);
        List<OperatingHours> operatingHours =
                operatingHoursRepository.findByBusinessIdOrderByDayOfWeekAsc(businessId);

        return responseGenerator.generateResponse(
                businessId,
                business.getBusinessName(),
                businessHours,
                operatingHours
        );
    }
}