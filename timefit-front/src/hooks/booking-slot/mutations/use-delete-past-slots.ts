import { useState } from 'react';
import { toast } from 'sonner';

import { bookingSlotService } from '@/services/booking-slot/booking-slot-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeletePastSlots(businessId: string) {
  const [loading, setLoading] = useState(false);

  const deletePastSlots = async (): Promise<number | null> => {
    try {
      setLoading(true);

      const result = await bookingSlotService.deletePastSlots(businessId);

      if (handleAuthError(result)) return null;

      if (!result.success) {
        toast.error(result.message || '과거 슬롯 삭제에 실패했습니다.');
        return null;
      }

      const count = result.data ?? 0;
      toast.success(`과거 슬롯 ${count}개가 삭제되었습니다.`);
      return count;
    } catch (err) {
      console.error('과거 슬롯 삭제 실패:', err);
      toast.error('과거 슬롯 삭제 중 오류가 발생했습니다.');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return { deletePastSlots, loading };
}
