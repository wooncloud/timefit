import { useCallback, useState } from 'react';
import { toast } from 'sonner';

import type { BusinessReservationItem } from '@/types/business/reservation';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';

/**
 * 캘린더 예약 데이터 fetch 담당
 */
export function useCalendarData(businessId: string, initialReservations: BusinessReservationItem[]) {
  const [allReservations, setAllReservations] = useState<BusinessReservationItem[]>(initialReservations);

  const fetchReservations = useCallback(async (start: string, end: string) => {
    try {
      const result = await businessReservationService.getReservations(businessId, {
        startDate: start,
        endDate: end,
        size: 100,
      });
      if (result.success && result.data) setAllReservations(result.data.reservations);
    } catch { toast.error('예약 데이터를 불러오는 데 실패했습니다.'); }
  }, [businessId]);

  return { allReservations, fetchReservations };
}