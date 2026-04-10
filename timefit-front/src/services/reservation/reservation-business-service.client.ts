import type {
  BusinessReservationActionHandlerResponse,
  GetBusinessReservationDetailHandlerResponse,
  GetBusinessReservationListHandlerResponse,
  GetReservationStatsHandlerResponse,
} from '@/types/business/reservation';

/**
 * 클라이언트 측 클래스: 사업자용 예약 액션 (Mutations + 상세 조회)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class BusinessReservationService {
  /**
   * 예약 상세 조회
   */
  async getReservationDetail(
    businessId: string,
    reservationId: string
  ): Promise<GetBusinessReservationDetailHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/reservation/${reservationId}`,
      { method: 'GET' }
    );
    return response.json();
  }

  /**
   * 예약 승인 (PENDING → CONFIRMED)
   */
  async approveReservation(
    businessId: string,
    reservationId: string,
    notes?: string
  ): Promise<BusinessReservationActionHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/reservation/${reservationId}/approve`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ notes }),
      }
    );
    return response.json();
  }

  /**
   * 예약 거절 (PENDING → REJECTED)
   */
  async rejectReservation(
    businessId: string,
    reservationId: string,
    notes?: string
  ): Promise<BusinessReservationActionHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/reservation/${reservationId}/reject`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ notes }),
      }
    );
    return response.json();
  }

  /**
   * 예약 완료 처리 (CONFIRMED → COMPLETED)
   */
  async completeReservation(
    businessId: string,
    reservationId: string,
    notes?: string
  ): Promise<BusinessReservationActionHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/reservation/${reservationId}/complete`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ notes }),
      }
    );
    return response.json();
  }

  /**
   * 노쇼 처리 (CONFIRMED → NO_SHOW)
   */
  async noShowReservation(
    businessId: string,
    reservationId: string,
    notes?: string
  ): Promise<BusinessReservationActionHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/reservation/${reservationId}/no-show`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ notes }),
      }
    );
    return response.json();
  }

  /**
   * 예약 목록 조회
   */
  async getReservations(
    businessId: string,
    filters: {
      status?: string;
      startDate?: string;
      endDate?: string;
      customerName?: string;
      page?: number;
      size?: number;
    }
  ): Promise<GetBusinessReservationListHandlerResponse> {
    const params = new URLSearchParams();
    if (filters.status) params.append('status', filters.status);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);
    if (filters.customerName) params.append('customerName', filters.customerName);
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size !== undefined) params.append('size', filters.size.toString());

    const response = await fetch(
      `/api/business/${businessId}/reservations?${params.toString()}`,
      { method: 'GET' }
    );
    return response.json();
  }

  /**
   * 예약 통계 조회
   */
  async getReservationStats(
    businessId: string,
    filters: {
      status?: string;
      customerName?: string;
      startDate?: string;
      endDate?: string;
    }
  ): Promise<GetReservationStatsHandlerResponse> {
    const params = new URLSearchParams();
    if (filters.status) params.append('status', filters.status);
    if (filters.customerName) params.append('customerName', filters.customerName);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await fetch(
      `/api/business/${businessId}/reservations/stats?${params.toString()}`,
      { method: 'GET' }
    );
    return response.json();
  }
}

export const businessReservationService = new BusinessReservationService();