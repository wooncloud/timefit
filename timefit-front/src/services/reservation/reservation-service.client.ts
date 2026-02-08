import type {
  CancelReservationHandlerResponse,
  CreateReservationHandlerResponse,
  CreateReservationRequest,
} from '@/types/customer/reservation';

/**
 * 클라이언트 측 클래스: 예약 관리 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class ReservationService {
  /**
   * 예약 생성 (API 라우트를 통한 클라이언트 측 호출)
   */
  async createReservation(
    data: CreateReservationRequest
  ): Promise<CreateReservationHandlerResponse> {
    const response = await fetch('/api/reservation', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });

    return response.json();
  }

  /**
   * 예약 취소 (API 라우트를 통한 클라이언트 측 호출)
   */
  async cancelReservation(
    reservationId: string,
    reason?: string
  ): Promise<CancelReservationHandlerResponse> {
    const response = await fetch(`/api/reservation/${reservationId}/cancel`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ reason }),
    });

    return response.json();
  }
}

export const reservationService = new ReservationService();
