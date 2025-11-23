package timefit.booking.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.dto.BookingSlotResponse;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotQueryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BookingSlot DTO 변환 유틸리티
 * - Entity → Response DTO 변환
 * - 현재 예약 수 조회 포함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingSlotDtoConverter {

    private final BookingSlotQueryRepository bookingSlotQueryRepository;

    /**
     * BookingSlot Entity List → BookingSlot Response DTO List 변환
     * - 각 슬롯의 현재 예약 수를 조회하여 DTO에 포함
     *
     * @param slots BookingSlot 엔티티 목록
     * @return BookingSlot Response DTO 목록
     */
    public List<BookingSlotResponse.BookingSlot> convertToResponseList(List<BookingSlot> slots) {
        return slots.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 단일 BookingSlot Entity → BookingSlot Response DTO 변환
     * - 현재 예약 수 조회 포함
     *
     * @param slot BookingSlot 엔티티
     * @return BookingSlot Response DTO
     */
    public BookingSlotResponse.BookingSlot convertToResponse(BookingSlot slot) {
        return BookingSlotResponse.BookingSlot.of(slot);
    }
}