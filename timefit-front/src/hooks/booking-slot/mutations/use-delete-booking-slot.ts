import { useState } from 'react';
import { toast } from 'sonner';

import { bookingSlotService } from '@/services/booking-slot/booking-slot-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeleteBookingSlot(businessId: string) {
  const [loading, setLoading] = useState(false);

  const deleteBookingSlot = async (slotId: string): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await bookingSlotService.deleteSlot(businessId, slotId);

      if (handleAuthError(result)) return false;

      if (!result.success) {
        toast.error(result.message || '슬롯 삭제에 실패했습니다.');
        return false;
      }

      toast.success('슬롯이 삭제되었습니다.');
      return true;
    } catch (err) {
      console.error('슬롯 삭제 실패:', err);
      toast.error('슬롯 삭제 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { deleteBookingSlot, loading };
}