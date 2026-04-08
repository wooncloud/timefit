// ─────────────────────────────────────────
// 사업자용 고객 목록 타입
// 백엔드 ReservationResponseDto.CustomerListItem에 대응
// ─────────────────────────────────────────

export type CustomerSortType = 'LAST_VISIT' | 'FIRST_VISIT' | 'TOTAL_VISITS' | 'NAME';
import type { ReservationStatus } from '@/types/business/reservation';

export interface BusinessCustomerItem {
  customerId: string;
  customerName: string;
  customerPhone: string;
  totalVisits: number;
  lastVisitDate: string;   // YYYY-MM-DD
  firstVisitDate: string;  // YYYY-MM-DD
  memo: string | null;
}

export interface BusinessCustomerList {
  customers: BusinessCustomerItem[];
  pagination: {
    currentPage: number;
    totalPages: number;
    totalElements: number;
    size: number;
    hasNext: boolean;
    hasPrevious: boolean;
  };
}

// ─────────────────────────────────────────
// 사업자용 고객 상세 타입
// 백엔드 ReservationResponseDto.CustomerDetail에 대응
// ─────────────────────────────────────────

export interface ReservationHistoryItem {
  reservationId: string;
  reservationDate: string;   // YYYY-MM-DD
  reservationTime: string;   // HH:mm:ss
  menuServiceName: string;
  status: ReservationStatus;
  reservationPrice: number;
}

export interface BusinessCustomerDetail {
  customerId: string;
  customerName: string;
  customerPhone: string;
  customerEmail: string | null;
  totalVisits: number;
  lastVisitDate: string;
  firstVisitDate: string;
  memo: string | null;
  recentReservations: ReservationHistoryItem[];
}

// ─────────────────────────────────────────
// 메모 응답 타입
// ─────────────────────────────────────────

export interface CustomerMemoResponse {
  customerId: string;
  memo: string | null;
}

// ─────────────────────────────────────────
// API 응답 타입 (백엔드 → Next.js 서버)
// ─────────────────────────────────────────

export interface GetBusinessCustomerListApiResponse {
  data?: BusinessCustomerList;
  message?: string;
}

export interface GetBusinessCustomerDetailApiResponse {
  data?: BusinessCustomerDetail;
  message?: string;
}

export interface UpsertCustomerMemoApiResponse {
  data?: CustomerMemoResponse;
  message?: string;
}

// ─────────────────────────────────────────
// 핸들러 응답 타입 (Next.js API → 클라이언트)
// ─────────────────────────────────────────

export interface GetBusinessCustomerListHandlerResponse {
  success: boolean;
  data?: BusinessCustomerList;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface GetBusinessCustomerDetailHandlerResponse {
  success: boolean;
  data?: BusinessCustomerDetail;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface UpsertCustomerMemoHandlerResponse {
  success: boolean;
  data?: CustomerMemoResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}