import type { OperatingHours } from '@/types/schedule/operating-hours';

import type { BusinessHours, WeekdayId } from './weekdays';

/**
 * dayOfWeek 숫자를 WeekdayId로 변환
 * 0=일요일, 1=월요일, ..., 6=토요일
 */
function dayOfWeekToWeekdayId(dayOfWeek: number): WeekdayId {
  const mapping: Record<number, WeekdayId> = {
    0: 'sun',
    1: 'mon',
    2: 'tue',
    3: 'wed',
    4: 'thu',
    5: 'fri',
    6: 'sat',
  };
  return mapping[dayOfWeek] || 'mon';
}

/**
 * 백엔드 OperatingHours를 프론트엔드 BusinessHours[]로 변환
 */
export function mapOperatingHoursToBusinessHours(
  operatingHours: OperatingHours
): BusinessHours[] {
  return operatingHours.schedules.map(schedule => ({
    id: dayOfWeekToWeekdayId(schedule.dayOfWeek),
    startTime: schedule.openTime || '09:00',
    endTime: schedule.closeTime || '18:00',
    isEnabled: !schedule.isClosed,
  }));
}
/**
 * WeekdayId를 dayOfWeek 숫자로 변환
 * sun=0, mon=1, ..., sat=6
 */
function weekdayIdToDayOfWeek(weekdayId: WeekdayId): number {
  const mapping: Record<WeekdayId, number> = {
    sun: 0,
    mon: 1,
    tue: 2,
    wed: 3,
    thu: 4,
    fri: 5,
    sat: 6,
  };
  return mapping[weekdayId];
}

/**
 * 프론트엔드 데이터를 백엔드 PUT 요청 형식으로 변환
 */
export function mapToUpdateOperatingHoursRequest(
  businessHours: BusinessHours[],
  bookingSlotsMap: Record<
    string,
    import('@/types/schedule/operating-hours').BookingTimeRange[]
  >
): import('@/types/schedule/operating-hours').UpdateOperatingHoursRequest {
  const schedules = businessHours.map(day => {
    const dayOfWeek = weekdayIdToDayOfWeek(day.id);
    const bookingSlots = bookingSlotsMap[day.id] || [];

    return {
      dayOfWeek,
      openTime: day.isEnabled ? day.startTime : null,
      closeTime: day.isEnabled ? day.endTime : null,
      isClosed: !day.isEnabled,
      bookingTimeRanges: bookingSlots.map(({ startTime, endTime }) => ({
        startTime,
        endTime,
      })),
    };
  });

  return { schedules };
}
