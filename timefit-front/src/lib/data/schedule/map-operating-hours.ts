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
