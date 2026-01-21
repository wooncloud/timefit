/**
 * 예약 가능 시간대
 */
export interface BookingTimeRange {
  id?: string; // Optional for client-side identification
  startTime: string; // HH:mm
  endTime: string; // HH:mm
}

/**
 * 요일별 스케줄 정보
 */
export interface DaySchedule {
  dayOfWeek: number; // 0=일요일, 1=월요일, ..., 6=토요일
  openTime: string | null; // HH:mm
  closeTime: string | null; // HH:mm
  isClosed: boolean; // 휴무일 여부
  bookingTimeRanges: BookingTimeRange[];
}

/**
 * 영업시간 정보
 */
export interface OperatingHours {
  businessId: string;
  businessName: string;
  schedules: DaySchedule[]; // 7개 요일
}

/**
 * API 응답 구조 (백엔드에서 직접 반환)
 */
export interface GetOperatingHoursApiResponse {
  data?: OperatingHours;
  message?: string;
}
/**
 * PUT API 요청: 예약 가능 시간대 입력
 */
interface BookingTimeRangeInput {
  startTime: string; // HH:mm
  endTime: string; // HH:mm
}

/**
 * PUT API 요청: 요일별 스케줄 입력
 */
export interface DayScheduleInput {
  dayOfWeek: number; // 0=일요일 ~ 6=토요일
  openTime: string | null; // HH:mm
  closeTime: string | null; // HH:mm
  isClosed: boolean;
  bookingTimeRanges: BookingTimeRangeInput[];
}

/**
 * PUT API 요청 바디
 */
export interface UpdateOperatingHoursRequest {
  schedules: DayScheduleInput[]; // 7개 요일 전체
}

/**
 * PUT API 응답
 */
export interface UpdateOperatingHoursResponse {
  success: boolean;
  data: OperatingHours;
}
