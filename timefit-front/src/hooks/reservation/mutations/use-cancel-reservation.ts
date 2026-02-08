import { useState } from 'react';
import { toast } from 'sonner';

import { reservationService } from '@/services/reservation/reservation-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useCancelReservation() {
  const [loading, setLoading] = useState(false);

  const cancelReservation = async (
    reservationId: string,
    reason?: string
  ): Promise<boolean> => {
    try {
      setLoading(true);
      const result = await reservationService.cancelReservation(
        reservationId,
        reason
      );

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '예약 취소에 실패했습니다.');
        return false;
      }

      toast.success('예약이 취소되었습니다.');
      return true;
    } catch (err) {
      console.error('예약 취소 실패:', err);
      toast.error('예약 취소 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { cancelReservation, loading };
}
