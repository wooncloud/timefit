import { useState } from 'react';
import { toast } from 'sonner';
import dayjs from 'dayjs';

import type {
  BusinessReservationDetail,
  BusinessReservationItem,
  PaginationInfo,
  ReservationStats,
} from '@/types/business/reservation';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';
import type { FilterValues } from '@/components/business/reservations/reservation-filter-toolbar';

interface UseReservationsProps {
  initialReservations: BusinessReservationItem[];
  initialPagination: PaginationInfo;
  initialStats: ReservationStats;
  businessId: string;
}

export function useReservations({
                                  initialReservations,
                                  initialPagination,
                                  initialStats,
                                  businessId,
                                }: UseReservationsProps) {
  const [reservations, setReservations] = useState<BusinessReservationItem[]>(initialReservations);
  const [pagination, setPagination] = useState<PaginationInfo>(initialPagination);
  const [stats, setStats] = useState<ReservationStats>(initialStats);
  const [isLoading, setIsLoading] = useState(false);
  const [detailModal, setDetailModal] = useState<{
    isOpen: boolean;
    detail: BusinessReservationDetail | null;
  }>({ isOpen: false, detail: null });

  const today = dayjs();
  const [currentFilters, setCurrentFilters] = useState<FilterValues>({
    startDate: today.subtract(15, 'day').format('YYYY-MM-DD'),
    endDate: today.add(15, 'day').format('YYYY-MM-DD'),
  });

  // 통계 갱신
  const refreshStats = async (filters: FilterValues) => {
    const result = await businessReservationService.getReservationStats(businessId, filters);
    if (result.success && result.data) setStats(result.data);
  };

  // 상세 보기
  const handleDetail = async (reservationId: string) => {
    const result = await businessReservationService.getReservationDetail(businessId, reservationId);
    if (!result.success || !result.data) {
      toast.error(result.message || '상세 정보를 불러오는 데 실패했습니다.');
      return;
    }
    setDetailModal({ isOpen: true, detail: result.data });
  };

  const closeDetailModal = () => setDetailModal({ isOpen: false, detail: null });

  // 필터 검색 — 목록 + 통계 동시 갱신
  const handleSearch = async (filters: FilterValues) => {
    try {
      setIsLoading(true);
      const [listResult, statsResult] = await Promise.all([
        businessReservationService.getReservations(businessId, { ...filters, page: 0, size: 20 }),
        businessReservationService.getReservationStats(businessId, filters),
      ]);
      if (!listResult.success) { toast.error('조회에 실패했습니다.'); return; }
      setCurrentFilters(filters);
      setReservations(listResult.data!.reservations);
      setPagination(listResult.data!.pagination);
      if (statsResult.success && statsResult.data) setStats(statsResult.data);
    } catch {
      toast.error('조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 페이지 이동
  const handlePageChange = async (page: number) => {
    try {
      setIsLoading(true);
      const result = await businessReservationService.getReservations(
        businessId, { ...currentFilters, page, size: 20 }
      );
      if (!result.success) { toast.error('페이지 로드에 실패했습니다.'); return; }
      setReservations(result.data!.reservations);
      setPagination(result.data!.pagination);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch {
      toast.error('페이지 로드 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 액션 핸들러
  const handleApprove = async (reservationId: string) => {
    const result = await businessReservationService.approveReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '승인에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'CONFIRMED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 승인했습니다.');
    await refreshStats(currentFilters);
  };

  const handleReject = async (reservationId: string) => {
    const result = await businessReservationService.rejectReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '거절에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'REJECTED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 거절했습니다.');
    await refreshStats(currentFilters);
  };

  const handleComplete = async (reservationId: string) => {
    const result = await businessReservationService.completeReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '완료 처리에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'COMPLETED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 완료 처리했습니다.');
    await refreshStats(currentFilters);
  };

  const handleNoShow = async (reservationId: string) => {
    const result = await businessReservationService.noShowReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '노쇼 처리에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'NO_SHOW' as const, requiresAction: false } : r
    ));
    toast.success('노쇼 처리했습니다.');
    await refreshStats(currentFilters);
  };

  return {
    // 상태
    reservations,
    pagination,
    stats,
    isLoading,
    detailModal,
    currentFilters,
    // 핸들러
    handleDetail,
    closeDetailModal,
    handleSearch,
    handlePageChange,
    handleApprove,
    handleReject,
    handleComplete,
    handleNoShow,
  };
}