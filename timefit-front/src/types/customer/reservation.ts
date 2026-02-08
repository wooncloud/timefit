/**
 * 예약 상태
 */
export type ReservationStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'REJECTED'
  | 'CANCELLED'
  | 'COMPLETED'
  | 'NO_SHOW';

/**
 * 예약 생성 요청
 */
export interface CreateReservationRequest {
  businessId: string;
  menuId: string;
  bookingSlotId?: string | null;
  reservationDate?: string | null; // YYYY-MM-DD
  reservationTime?: string | null; // HH:mm:ss
  durationMinutes: number;
  totalPrice: number;
  customerName: string;
  customerPhone: string;
}

/**
 * 고객용 예약 상세
 */
export interface CustomerReservation {
  reservationId: string;
  reservationNumber: string;
  status: ReservationStatus;
  createdAt: string;
  updatedAt: string;
  cancelledAt: string | null;
  businessId: string;
  businessName: string;
  businessAddress: string;
  businessContactPhone: string;
  businessLogoUrl: string | null;
  reservationDate: string; // YYYY-MM-DD
  reservationTime: string; // HH:mm:ss
  reservationPrice: number;
  reservationDuration: number;
  menuServiceName: string;
  menuName?: string; // 메뉴 이름 (선택적)
  notes: string | null;
  customerMemo?: string | null; // 고객 메모
  businessNotes?: string | null; // 업체 메시지
  customerNameSnapshot: string;
  customerPhoneSnapshot: string;
}

/**
 * 고객용 예약 목록 아이템
 */
export interface CustomerReservationItem {
  reservationId: string;
  reservationNumber: string;
  status: ReservationStatus;
  businessId: string;
  businessName: string;
  businessLogoUrl: string | null;
  reservationDate: string;
  reservationTime: string;
  reservationDuration: number;
  reservationPrice: number;
  menuName?: string; // 메뉴 이름
  customerMemo?: string | null; // 고객 메모
  businessNotes?: string | null; // 업체 메시지
  createdAt: string;
  updatedAt: string;
}

/**
 * 페이지네이션 정보
 */
export interface PaginationInfo {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  size: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/**
 * 고객용 예약 목록
 */
export interface CustomerReservationList {
  reservations: CustomerReservationItem[];
  pagination: PaginationInfo;
}

// API 응답 타입 (백엔드 → Next.js 서버)
export interface CreateReservationApiResponse {
  data?: CustomerReservation;
  message?: string;
}

export interface GetCustomerReservationListApiResponse {
  data?: CustomerReservationList;
  message?: string;
}

export interface GetCustomerReservationDetailApiResponse {
  data?: CustomerReservation;
  message?: string;
}

/**
 * 예약 액션 결과 (취소 등)
 */
export interface ReservationActionResult {
  reservationId: string;
  previousStatus: ReservationStatus;
  currentStatus: ReservationStatus;
  message: string;
  actionAt: string;
}

export interface CancelReservationApiResponse {
  data?: ReservationActionResult;
  message?: string;
}

// 핸들러 응답 타입 (Next.js API → 클라이언트)
export interface CreateReservationHandlerResponse {
  success: boolean;
  data?: CustomerReservation;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface CancelReservationHandlerResponse {
  success: boolean;
  data?: { reservationId: string; message: string };
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
