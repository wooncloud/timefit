import type { EventInput } from '@fullcalendar/core';
import dayjs from 'dayjs';

import type { BusinessReservationItem } from '@/types/business/reservation';
import { RESERVATION_STATUS_FILTERS } from '@/lib/constants/reservation-status';

/**
 * 사업자용 예약 목록을 FullCalendar 이벤트 형식으로 변환
 * @param reservations 예약 목록
 * @param activeStatuses 표시할 상태 집합
 */
export function toCalendarEvents(
  reservations: BusinessReservationItem[],
  activeStatuses: Set<string>
): EventInput[] {
  return reservations
    .filter(r => activeStatuses.has(r.status))
    .map(r => {
      const filter = RESERVATION_STATUS_FILTERS.find(f => f.value === r.status);
      return {
        id: r.reservationId,
        title: `${r.customerName} · ${r.reservationDuration}분`,
        start: `${r.reservationDate}T${r.reservationTime}`,
        end: dayjs(`${r.reservationDate}T${r.reservationTime}`)
          .add(r.reservationDuration, 'minute')
          .format('YYYY-MM-DDTHH:mm:ss'),
        backgroundColor: filter?.eventColor ?? '#e5e7eb',
        borderColor: filter?.eventColor ?? '#e5e7eb',
        textColor: filter?.eventTextColor ?? '#374151',
      };
    });
}