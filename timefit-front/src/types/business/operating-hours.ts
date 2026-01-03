import type { WeekdayId } from '@/lib/data/schedule/weekdays';

/**
 * WeekdayId를 API에서 요구하는 숫자(0-6)로 변환
 * 0 = 월요일, 1 = 화요일, ..., 6 = 일요일
 */
export function weekdayIdToDayOfWeek(id: WeekdayId): number {
  const mapping: Record<WeekdayId, number> = {
    mon: 0,
    tue: 1,
    wed: 2,
    thu: 3,
    fri: 4,
    sat: 5,
    sun: 6,
  };
  return mapping[id];
}

/**
 * API 응답 타입: 운영 시간 토글
 */
export interface ToggleOperatingHoursApiResponse {
  data?: {
    dayOfWeek: number;
    isEnabled: boolean;
  };
  message?: string;
}

/**
 * Next.js 핸들러 응답 타입: 운영 시간 토글
 */
export interface ToggleOperatingHoursHandlerResponse {
  success: boolean;
  data?: {
    dayOfWeek: number;
    isEnabled: boolean;
  };
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
