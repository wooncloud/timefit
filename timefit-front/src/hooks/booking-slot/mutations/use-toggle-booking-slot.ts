import { useState } from 'react';
import { toast } from 'sonner';

import type { BookingSlot } from '@/types/booking-slot/booking-slot';
import { bookingSlotService } from '@/services/booking-slot/booking-slot-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useToggleBookingSlot(businessId: string) {
  const [loading, setLoading] = useState(false);

  const toggleSlot = async (
    slotId: string,
    activate: boolean
  ): Promise<BookingSlot | null> => {
    try {
      setLoading(true);

      const result = activate
        ? await bookingSlotService.activateSlot(businessId, slotId)
        : await bookingSlotService.deactivateSlot(businessId, slotId);

      if (handleAuthError(result)) return null;

      if (!result.success || !result.data) {
        toast.error(
          result.message ||
            `슬롯 ${activate ? '활성화' : '비활성화'}에 실패했습니다.`
        );
        return null;
      }

      toast.success(`슬롯이 ${activate ? '활성화' : '비활성화'}되었습니다.`);
      return result.data;
    } catch (err) {
      console.error('슬롯 토글 실패:', err);
      toast.error('슬롯 상태 변경 중 오류가 발생했습니다.');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { toggleSlot, loading };
}
