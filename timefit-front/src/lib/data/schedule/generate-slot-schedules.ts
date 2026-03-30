import type { DailySlotSchedule, SlotTimeRange } from '@/types/booking-slot/booking-slot';
import type { OperatingHours } from '@/types/schedule/operating-hours';

/**
 * operating hours 기반으로 날짜 범위에 대한 슬롯 생성 스케줄을 만든다.
 * 클라이언트 측 미리보기 및 POST /booking-slot 요청 body 생성용.
 */
export function generateSlotSchedules(
  startDate: string,
  endDate: string,
  operatingHours: OperatingHours
): DailySlotSchedule[] {
  const schedules: DailySlotSchedule[] = [];
  const start = new Date(startDate);
  const end = new Date(endDate);

  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const dayOfWeek = d.getDay(); // 0=일, 1=월, ..., 6=토
    const daySchedule = operatingHours.schedules.find(
      s => s.dayOfWeek === dayOfWeek
    );

    if (!daySchedule || daySchedule.isClosed) continue;

    let timeRanges: SlotTimeRange[];

    if (daySchedule.bookingTimeRanges.length > 0) {
      timeRanges = daySchedule.bookingTimeRanges.map(r => ({
        startTime: r.startTime,
        endTime: r.endTime,
      }));
    } else if (daySchedule.openTime && daySchedule.closeTime) {
      timeRanges = [
        { startTime: daySchedule.openTime, endTime: daySchedule.closeTime },
      ];
    } else {
      continue;
    }

    const dateStr = d.toLocaleDateString('sv-SE'); // YYYY-MM-DD
    schedules.push({ date: dateStr, timeRanges });
  }

  return schedules;
}

/**
 * 슬롯 간격을 기반으로 예상 슬롯 개수를 계산한다.
 */
export function countSlotsForSchedules(
  schedules: DailySlotSchedule[],
  intervalMinutes: number
): number {
  let total = 0;

  for (const schedule of schedules) {
    for (const range of schedule.timeRanges) {
      const [startH, startM] = range.startTime.split(':').map(Number);
      const [endH, endM] = range.endTime.split(':').map(Number);
      const startMinutes = startH * 60 + startM;
      const endMinutes = endH * 60 + endM;
      const duration = endMinutes - startMinutes;

      if (duration > 0 && intervalMinutes > 0) {
        total += Math.floor(duration / intervalMinutes);
      }
    }
  }

  return total;
}