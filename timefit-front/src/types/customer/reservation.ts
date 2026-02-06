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
 * 고객용 예약 상세
 */
export interface CustomerReservation {
  reservationId: string;
  reservationNumber: string;
  status: ReservationStatus;
  createdAt: string;
  updatedAt: string;
  cancelledAt?: string;
  businessId: string;
  businessName: string;
  businessAddress: string;
  businessContactPhone: string;
  businessLogoUrl?: string;
  reservationDate: string;
  reservationTime: string;
  menuId: string;
  menuName: string;
  menuPrice: number;
  menuDuration: number;
  customerMemo?: string;
  businessNotes?: string;
}

/**
 * 예약 목록 응답 (페이징)
 */
export interface CustomerReservationList {
  reservations: CustomerReservation[];
  totalCount: number;
  page: number;
  size: number;
  totalPages: number;
}

/**
 * API 응답 타입 (백엔드 → Next.js 서버)
 */
export interface GetReservationsApiResponse {
  data?: CustomerReservationList;
  message?: string;
}

/**
 * 예약 취소 핸들러 응답 (Next.js API → 클라이언트)
 */
export interface CancelReservationHandlerResponse {
  success: boolean;
  data?: { reservationId: string; message: string };
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
