package timefit.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.exception.schedule.ScheduleErrorCode;
import timefit.exception.schedule.ScheduleException;
import timefit.schedule.dto.ScheduleRequestDto;
import timefit.schedule.dto.ScheduleResponseDto;
import timefit.schedule.factory.ScheduleResponseFactory;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessOperatingHours;
import timefit.business.entity.UserBusinessRole;
import timefit.business.entity.DayOfWeek;
import timefit.business.repository.BusinessOperatingHoursRepository;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.common.ResponseData;
import timefit.common.entity.BusinessRole;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;
import timefit.reservation.entity.ReservationTimeSlot;
import timefit.reservation.repository.ReservationTimeSlotRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final BusinessOperatingHoursRepository businessOperatingHoursRepository;
    private final ReservationTimeSlotRepository reservationTimeSlotRepository;
    private final ScheduleResponseFactory responseFactory;

    /**
     * 영업시간 설정
     * 권한: OWNER, MANAGER만 가능
     */
    @Transactional
    public ResponseData<ScheduleResponseDto.OperatingHoursResult> setOperatingHours(
            UUID businessId, ScheduleRequestDto.SetOperatingHours request, UUID currentUserId) {

        log.info("영업시간 설정 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 권한 검증
        Business business = validateBusinessExists(businessId);
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 기존 영업시간 삭제
        List<BusinessOperatingHours> existingHours = businessOperatingHoursRepository
                .findByBusinessIdOrderByDayOfWeek(businessId);
        if (!existingHours.isEmpty()) {
            businessOperatingHoursRepository.deleteAll(existingHours);
        }

        // 3. 새로운 영업시간 생성
        List<BusinessOperatingHours> newHours = new ArrayList<>();
        for (ScheduleRequestDto.BusinessHour hourRequest : request.getBusinessHours()) {
            BusinessOperatingHours hour = createBusinessOperatingHours(business, hourRequest);
            newHours.add(hour);
        }

        List<BusinessOperatingHours> savedHours = businessOperatingHoursRepository.saveAll(newHours);

        // 4. 응답 생성
        ScheduleResponseDto.OperatingHoursResult response =
                responseFactory.createOperatingHoursResponse(business, savedHours);

        log.info("영업시간 설정 완료: businessId={}, 설정된 요일 수={}", businessId, savedHours.size());
        return ResponseData.of(response);
    }

    /**
     * 영업시간 조회
     */
    public ResponseData<ScheduleResponseDto.OperatingHoursResult> getOperatingHours(UUID businessId) {
        log.info("영업시간 조회: businessId={}", businessId);

        // 1. 업체 존재 확인
        Business business = validateBusinessExists(businessId);

        // 2. 영업시간 조회
        List<BusinessOperatingHours> hours = businessOperatingHoursRepository
                .findByBusinessIdOrderByDayOfWeek(businessId);

        // 3. 응답 생성
        ScheduleResponseDto.OperatingHoursResult response =
                responseFactory.createOperatingHoursResponse(business, hours);

        log.info("영업시간 조회 완료: businessId={}, 영업시간 수={}", businessId, hours.size());
        return ResponseData.of(response);
    }


//    /**
//     * 예약 슬롯 생성 (단일 생성)
//     * 권한: OWNER, MANAGER만 가능
//     * @deprecated
//     */
//    @Transactional
//    public ResponseData<ScheduleResponseDto.SlotDetail> createSlot(
//            UUID businessId, ScheduleRequestDto.CreateSlot request, UUID currentUserId) {
//
//        log.info("예약 슬롯 생성 시작: businessId={}, userId={}, date={}, time={}",
//                businessId, currentUserId, request.getSlotDate(), request.getStartTime());
//
//        // 1. 권한 검증
//        Business business = validateBusinessExists(businessId);
//        validateManagerOrOwnerRole(currentUserId, businessId);
//
//        // 2. 슬롯 생성 검증
//        validateSlotCreation(request, business);
//
//        // 3. 슬롯 생성
//        ReservationTimeSlot slot = createReservationTimeSlot(request, business);
//        ReservationTimeSlot savedSlot = reservationTimeSlotRepository.save(slot);
//
//        // 4. 응답 생성
//        ScheduleResponseDto.SlotDetail response = responseFactory.createSlotDetailResponse(savedSlot);
//
//        log.info("예약 슬롯 생성 완료: slotId={}", savedSlot.getId());
//        return ResponseData.of(response);
//    }

    /**
     * 예약 슬롯 생성
     * 권한: OWNER, MANAGER만 가능
     */
    @Transactional
    public ResponseData<ScheduleResponseDto.SlotCreationResult> createMultipleSlots(
            UUID businessId, ScheduleRequestDto.CreateMultipleSlots request, UUID currentUserId) {

        log.info("예약 슬롯 생성 시작: businessId={}, userId={}, slotCount={}",
                businessId, currentUserId, request.getSlots().size());

        // 1. 권한 검증
        Business business = validateBusinessExists(businessId);
        validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 슬롯 일괄 생성
        List<ReservationTimeSlot> createdSlots = new ArrayList<>();

        for (ScheduleRequestDto.CreateSlot slotRequest : request.getSlots()) {
            validateSlotCreation(slotRequest, business);
            ReservationTimeSlot slot = createReservationTimeSlot(slotRequest, business);
            ReservationTimeSlot savedSlot = reservationTimeSlotRepository.save(slot);
            createdSlots.add(savedSlot);
        }

        // 3. 응답 생성
        ScheduleResponseDto.SlotCreationResult response = responseFactory.createSlotCreationResult(
                request.getSlots().size(), createdSlots);

        log.info("다중 예약 슬롯 생성 완료: 성공={}", createdSlots.size());
        return ResponseData.of(response);
    }

    /**
     * 날짜별 예약 슬롯 조회 (고객용)
     */
    public ResponseData<ScheduleResponseDto.DailySlotsResult> getDailySlots(
            UUID businessId, LocalDate date) {

        log.info("날짜별 예약 슬롯 조회: businessId={}, date={}", businessId, date);

        // 1. 업체 존재 확인
        Business business = validateBusinessExists(businessId);

        // 2. 영업 여부 확인
        Boolean isBusinessOpen = checkBusinessOpen(businessId, date);

        // 3. 예약 가능한 슬롯 조회
        List<ReservationTimeSlot> slots = reservationTimeSlotRepository
                .findAvailableSlotsByBusinessAndDate(businessId, date);

        // 4. 응답 생성
        ScheduleResponseDto.DailySlotsResult response =
                responseFactory.createDailySlotsResponse(date, slots, isBusinessOpen);

        log.info("날짜별 예약 슬롯 조회 완료: slotCount={}", slots.size());
        return ResponseData.of(response);
    }

    // --- Private  ---

    /**
     * BusinessOperatingHours 생성
     */
    private BusinessOperatingHours createBusinessOperatingHours(Business business,
                                                                ScheduleRequestDto.BusinessHour hourRequest) {
        return BusinessOperatingHours.createOperatingHours(
                business,
                DayOfWeek.fromValue(hourRequest.getDayOfWeek()), // 커스텀 DayOfWeek 사용
                hourRequest.getOpenTime(),
                hourRequest.getCloseTime(),
                hourRequest.getIsClosed()
        );
    }

    private Business validateBusinessExists(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    private void validateManagerOrOwnerRole(UUID userId, UUID businessId) {
        UserBusinessRole userRole = userBusinessRoleRepository
                .findByUserIdAndBusinessIdAndIsActive(userId, businessId, true)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_BUSINESS_MEMBER));

        if (userRole.getRole() == BusinessRole.MEMBER) {
            throw new BusinessException(BusinessErrorCode.INSUFFICIENT_PERMISSION);
        }
    }

    private void validateSlotCreation(ScheduleRequestDto.CreateSlot request, Business business) {
        // 1. 과거 날짜 방지
        if (request.getSlotDate().isBefore(LocalDate.now())) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_PAST_DATE);
        }

        // 2. 시간 순서 검증
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_INVALID_TIME);
        }

        // 3. 영업시간 내 확인
        validateWithinBusinessHours(business.getId(), request.getSlotDate(), request.getStartTime(), request.getEndTime());

        // 4. 중복 슬롯 확인
        boolean exists = reservationTimeSlotRepository.existsByBusinessIdAndSlotDateAndStartTime(
                business.getId(), request.getSlotDate(), request.getStartTime());
        if (exists) {
            throw new BusinessException(BusinessErrorCode.AVAILABLE_SLOT_CONFLICT);
        }
    }

    private void validateWithinBusinessHours(UUID businessId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        java.time.DayOfWeek javaDayOfWeek = date.getDayOfWeek();

        List<BusinessOperatingHours> operatingHours = businessOperatingHoursRepository
                .findByBusinessIdOrderByDayOfWeek(businessId);

        BusinessOperatingHours todayHours = operatingHours.stream()
                .filter(hours -> hours.getDayOfWeek().matches(javaDayOfWeek))
                .findFirst()
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_CLOSED));

        if (todayHours.getIsClosed()) {
            throw new BusinessException(BusinessErrorCode.BUSINESS_CLOSED);
        }

        if (startTime.isBefore(todayHours.getOpenTime()) || endTime.isAfter(todayHours.getCloseTime())) {
            throw new ScheduleException(ScheduleErrorCode.AVAILABLE_SLOT_OUTSIDE_BUSINESS_HOURS);
        }
    }

    private Boolean checkBusinessOpen(UUID businessId, LocalDate date) {
        java.time.DayOfWeek javaDayOfWeek = date.getDayOfWeek();

        List<BusinessOperatingHours> operatingHours = businessOperatingHoursRepository
                .findByBusinessIdOrderByDayOfWeek(businessId);

        return operatingHours.stream()
                .filter(hours -> hours.getDayOfWeek().matches(javaDayOfWeek))
                .findFirst()
                .map(hours -> !hours.getIsClosed())
                .orElse(false);
    }



    private ReservationTimeSlot createReservationTimeSlot(ScheduleRequestDto.CreateSlot request, Business business) {
        return ReservationTimeSlot.createSlot(
                business,
                request.getSlotDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCapacity()
        );
    }
}