/**
 * 예약 슬롯 (BookingSlot) 관련 타입 정의
 * 백엔드 BookingSlotResponse / BookingSlotRequest에 대응
 */

/** 개별 예약 슬롯 */
export interface BookingSlot {
  slotId: string;
  businessId: string;
  menuId: string;
  menuName: string;
  slotDate: string; // YYYY-MM-DD
  startTime: string; // HH:mm
  endTime: string; // HH:mm
  isAvailable: boolean;
}

/** 슬롯 목록 응답 */
export interface BookingSlotList {
  businessId: string;
  startDate: string;
  endDate: string;
  slots: BookingSlot[];
}

/** 슬롯 생성 결과 */
export interface BookingSlotCreationResult {
  totalRequested: number;
  created: number;
  skipped: number;
}

/** 슬롯 생성 시 시간 범위 */
export interface SlotTimeRange {
  startTime: string; // HH:mm
  endTime: string; // HH:mm
}

/** 슬롯 생성 시 일별 스케줄 */
export interface DailySlotSchedule {
  date: string; // YYYY-MM-DD
  timeRanges: SlotTimeRange[];
}

/** 슬롯 벌크 생성 요청 */
export interface CreateBookingSlotsRequest {
  menuId: string;
  slotIntervalMinutes: number;
  schedules: DailySlotSchedule[];
}

// --- Handler Response 타입 (Next.js API Route → Client) ---

export interface CreateBookingSlotsHandlerResponse {
  success: boolean;
  data?: BookingSlotCreationResult;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface GetBookingSlotsHandlerResponse {
  success: boolean;
  data?: BookingSlotList;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface DeleteBookingSlotHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface ToggleBookingSlotHandlerResponse {
  success: boolean;
  data?: BookingSlot;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface DeletePastSlotsHandlerResponse {
  success: boolean;
  data?: number;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}