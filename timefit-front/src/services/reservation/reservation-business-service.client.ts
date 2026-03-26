import type { BusinessReservationActionHandlerResponse } from '@/types/business/reservation';

/**
 * 클라이언트 측 클래스: 사업자용 예약 액션 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class BusinessReservationService {
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
}

export const businessReservationService = new BusinessReservationService();