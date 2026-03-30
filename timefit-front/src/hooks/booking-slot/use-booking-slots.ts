import { useCallback, useEffect, useState } from 'react';

import type { BookingSlot } from '@/types/booking-slot/booking-slot';
import { bookingSlotService } from '@/services/booking-slot/booking-slot-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useBookingSlots(businessId: string, date: string) {
  const [slots, setSlots] = useState<BookingSlot[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchSlots = useCallback(async () => {
    if (!date) {
      setSlots([]);
      return;
    }

    try {
      setLoading(true);
      const result = await bookingSlotService.getSlotsByDate(businessId, date);

      if (handleAuthError(result)) return;

      if (result.success && result.data) {
        setSlots(result.data.slots);
      } else {
        setSlots([]);
      }
    } catch (err) {
      console.error('슬롯 조회 실패:', err);
      setSlots([]);
    } finally {
      setLoading(false);
    }
  }, [businessId, date]);

  useEffect(() => {
    fetchSlots();
  }, [fetchSlots]);

  return { slots, loading, refetch: fetchSlots };
}
