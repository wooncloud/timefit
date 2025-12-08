package timefit.menu.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.service.dto.DailySlotSchedule;
import timefit.booking.service.helper.BookingSlotCreationHelper;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.util.List;

/**
 * Menu → BookingSlot 생성 헬퍼
 * - Menu 생성/수정 시 BookingSlot 생성 지원
 * - Menu 전용 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuBookingSlotHelper {

    private final BookingSlotCreationHelper bookingSlotCreationHelper;
    private final BookingSlotValidator bookingSlotValidator;
    private final timefit.booking.service.BookingSlotCommandService bookingSlotCommandService;
    private final timefit.menu.service.converter.MenuScheduleConverter menuScheduleConverter;

    /**
     * Menu 생성 시 BookingSlot 생성
     * [처리 흐름]
     * 1. Menu 생성 조건 검증
     * 2. 조건 미충족 시 종료
     * 3. Menu DTO → BookingSlot 스케줄 변환 (Converter)
     * 4. BookingSlot 생성 위임 (Helper)
     * [트랜잭션]
     * - MANDATORY: Menu 생성과 같은 트랜잭션
     *
     * @param menu 생성된 Menu
     * @param request Menu 생성 요청 DTO
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void generateForMenu(Menu menu, MenuRequestDto.CreateUpdateMenu request) {
        log.info("Menu에서 BookingSlot 생성 시작: menuId={}", menu.getId());

        // 1. Menu 생성 조건 검증
        bookingSlotValidator.validateCreationFromMenu(menu, request);

        // 2. ONDEMAND_BASED 또는 autoGenerateSlots=false면 종료
        if (request.orderType() != OrderType.RESERVATION_BASED ||
                !Boolean.TRUE.equals(request.autoGenerateSlots())) {
            log.debug("BookingSlot 생성 조건 미충족: menuId={}", menu.getId());
            return;
        }

        // 3. Menu DTO → BookingSlot 스케줄 변환 (Converter)
        List<DailySlotSchedule> schedules = menuScheduleConverter
                .convertToBookingSlotSchedules(request);

        // 4. BookingSlot 생성 위임 (Helper)
        bookingSlotCreationHelper.createSlots(
                menu.getBusiness(),
                menu,
                schedules,
                request.slotSettings().slotIntervalMinutes()
        );

        log.info("Menu에서 BookingSlot 생성 완료: menuId={}", menu.getId());
    }

    /**
     * Menu 수정 시 BookingSlot 재생성
     * [처리 흐름]
     * 1. 재생성 필요 여부 확인
     * 2. 기존 BookingSlot 삭제 (CommandService)
     * 3. generateForMenu() 재사용
     * [트랜잭션]
     * - MANDATORY: Menu 수정과 같은 트랜잭션
     *
     * @param menu 수정된 Menu
     * @param request Menu 수정 요청 DTO
     * @param oldDurationMinutes 수정 전 durationMinutes
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void regenerateForMenu(
            Menu menu,
            MenuRequestDto.CreateUpdateMenu request,
            Integer oldDurationMinutes) {

        log.info("Menu 수정 - BookingSlot 재생성 확인: menuId={}", menu.getId());

        // 1. 재생성 필요 여부 확인
        if (!bookingSlotValidator.shouldRegenerate(request, oldDurationMinutes)) {
            log.debug("durationMinutes 변경 없음 - 재생성 생략: menuId={}", menu.getId());
            return;
        }

        log.info("durationMinutes 변경 감지 - 재생성 시작: menuId={}, {}분 → {}분",
                menu.getId(), oldDurationMinutes, request.durationMinutes());

        // 2. 기존 BookingSlot 삭제
        bookingSlotCommandService.deleteSlotsForMenu(
                menu.getBusiness().getId(),
                menu.getId()
        );

        // 3. 재생성
        generateForMenu(menu, request);

        log.info("Menu 수정 - BookingSlot 재생성 완료: menuId={}", menu.getId());
    }
}