import { useState } from 'react';
import { toast } from 'sonner';

import type { BusinessReservationDetail } from '@/types/business/reservation';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';

/**
 * 캘린더 이벤트 클릭 시 예약 상세 모달 담당
 */
export function useCalendarModal(businessId: string) {
  const [detailModal, setDetailModal] = useState<{
    isOpen: boolean;
    detail: BusinessReservationDetail | null;
  }>({ isOpen: false, detail: null });

  const handleEventClick = async (reservationId: string) => {
    const result = await businessReservationService.getReservationDetail(businessId, reservationId);
    if (!result.success || !result.data) {
      toast.error('상세 정보를 불러오는 데 실패했습니다.');
      return;
    }
    setDetailModal({ isOpen: true, detail: result.data });
  };

  const closeDetailModal = () => setDetailModal({ isOpen: false, detail: null });

  return { detailModal, handleEventClick, closeDetailModal };
}