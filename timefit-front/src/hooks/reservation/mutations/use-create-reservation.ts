import { useState } from 'react';
import { toast } from 'sonner';

import type {
  CreateReservationRequest,
  CustomerReservation,
} from '@/types/customer/reservation';
import { reservationService } from '@/services/reservation/reservation-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useCreateReservation() {
  const [loading, setLoading] = useState(false);

  const createReservation = async (
    data: CreateReservationRequest
  ): Promise<CustomerReservation | null> => {
    try {
      setLoading(true);
      const result = await reservationService.createReservation(data);

      if (handleAuthError(result)) {
        return null;
      }

      if (!result.success || !result.data) {
        toast.error(result.message || '예약 생성에 실패했습니다.');
        return null;
      }

      toast.success('예약이 완료되었습니다.');
      return result.data;
    } catch (err) {
      console.error('예약 생성 실패:', err);
      toast.error('예약 생성 중 오류가 발생했습니다.');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { createReservation, loading };
}
