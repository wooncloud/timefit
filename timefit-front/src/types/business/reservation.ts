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
 * 예약 액션 결과 (승인·거절·완료·노쇼)
 */
export interface ReservationActionResult {
    reservationId: string;
    previousStatus: ReservationStatus;
    currentStatus: ReservationStatus;
    message: string;
    actionAt: string;
}

// ─────────────────────────────────────────
// 사업자용 예약 타입
// ─────────────────────────────────────────

/**
 * 사업자용 예약 목록 아이템
 */
export interface BusinessReservationItem {
    reservationId: string;
    reservationNumber: string;
    status: ReservationStatus;
    customerId: string;
    customerName: string;
    customerPhone: string;
    reservationDate: string;      // YYYY-MM-DD
    reservationTime: string;      // HH:mm:ss
    reservationDuration: number;  // 분
    reservationPrice: number;
    requiresAction: boolean;      // PENDING 상태 등 조치 필요 여부
    createdAt: string;
}

/**
 * 사업자용 예약 목록
 */
export interface BusinessReservationList {
    businessId: string;
    businessName: string;
    businessAddress: string;
    businessContactPhone: string;
    reservations: BusinessReservationItem[];
    pagination: PaginationInfo;
}

// ─────────────────────────────────────────
// API 응답 타입 (백엔드 → Next.js 서버)
// ─────────────────────────────────────────

export interface GetBusinessReservationListApiResponse {
    data?: BusinessReservationList;
    message?: string;
}

export interface BusinessReservationActionApiResponse {
    data?: ReservationActionResult;
    message?: string;
}

// ─────────────────────────────────────────
// 핸들러 응답 타입 (Next.js API → 클라이언트)
// ─────────────────────────────────────────

export interface GetBusinessReservationListHandlerResponse {
    success: boolean;
    data?: BusinessReservationList;
    message?: string;
    requiresLogout?: boolean;
    redirectTo?: string;
}

export interface BusinessReservationActionHandlerResponse {
    success: boolean;
    data?: ReservationActionResult;
    message?: string;
    requiresLogout?: boolean;
    redirectTo?: string;
}