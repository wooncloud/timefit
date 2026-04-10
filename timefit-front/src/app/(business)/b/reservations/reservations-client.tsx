'use client';

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
import {
  ReservationFilterToolbar,
  type FilterValues,
} from '@/components/business/reservations/reservation-filter-toolbar';
import { ReservationDetailModal } from '@/components/business/reservations/reservation-detail-modal';
import { ReservationPagination } from '@/components/business/reservations/reservation-pagination';
import { ReservationStatsCards } from '@/components/business/reservations/reservation-stats-cards';
import { ReservationTableBody } from '@/components/business/reservations/reservation-table-body';
import { ReservationTableHeader } from '@/components/business/reservations/reservation-table-header';
import { Card, CardContent } from '@/components/ui/card';
import { Table } from '@/components/ui/table';

interface ReservationsClientProps {
  initialReservations: BusinessReservationItem[];
  initialPagination: PaginationInfo;
  initialStats: ReservationStats;
  businessId: string;
}

export function ReservationsClient({
                                     initialReservations,
                                     initialPagination,
                                     initialStats,
                                     businessId,
                                   }: ReservationsClientProps) {
  const [reservations, setReservations] = useState<BusinessReservationItem[]>(initialReservations);
  const [pagination, setPagination] = useState<PaginationInfo>(initialPagination);
  const [stats, setStats] = useState<ReservationStats>(initialStats);
  const [isLoading, setIsLoading] = useState(false);

  // 상세 모달
  const [detailModal, setDetailModal] = useState<{
    isOpen: boolean;
    detail: BusinessReservationDetail | null;
  }>({ isOpen: false, detail: null });

  // page.tsx SSR과 동일한 기본값 ±15일
  const today = dayjs();
  const [currentFilters, setCurrentFilters] = useState<FilterValues>({
    startDate: today.subtract(15, 'day').format('YYYY-MM-DD'),
    endDate: today.add(15, 'day').format('YYYY-MM-DD'),
  });

  // 상세 보기
  const handleDetail = async (reservationId: string) => {
    const result = await businessReservationService.getReservationDetail(businessId, reservationId);
    if (!result.success || !result.data) {
      toast.error(result.message || '상세 정보를 불러오는 데 실패했습니다.');
      return;
    }
    setDetailModal({ isOpen: true, detail: result.data });
  };

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

  // 통계 갱신
  const refreshStats = async () => {
    const result = await businessReservationService.getReservationStats(businessId, currentFilters);
    if (result.success && result.data) setStats(result.data);
  };

  // 액션 핸들러
  const handleApprove = async (reservationId: string) => {
    const result = await businessReservationService.approveReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '승인에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'CONFIRMED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 승인했습니다.');
    await refreshStats();
  };

  const handleReject = async (reservationId: string) => {
    const result = await businessReservationService.rejectReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '거절에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'REJECTED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 거절했습니다.');
    await refreshStats();
  };

  const handleComplete = async (reservationId: string) => {
    const result = await businessReservationService.completeReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '완료 처리에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'COMPLETED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 완료 처리했습니다.');
    await refreshStats();
  };

  const handleNoShow = async (reservationId: string) => {
    const result = await businessReservationService.noShowReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '노쇼 처리에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
      r.reservationId === reservationId ? { ...r, status: 'NO_SHOW' as const, requiresAction: false } : r
    ));
    toast.success('노쇼 처리했습니다.');
    await refreshStats();
  };

  return (
    <div className="space-y-6">
      <ReservationFilterToolbar onSearch={handleSearch} />

      <ReservationStatsCards stats={stats} />

      <Card>
        <CardContent className="pt-4">
          <Table>
            <ReservationTableHeader />
            <ReservationTableBody
              reservations={reservations}
              onDetail={handleDetail}
              onApprove={handleApprove}
              onReject={handleReject}
              onComplete={handleComplete}
              onNoShow={handleNoShow}
            />
          </Table>
        </CardContent>
      </Card>

      <ReservationPagination
        pagination={pagination}
        isLoading={isLoading}
        onPageChange={handlePageChange}
      />

      {/* 상세 모달 */}
      <ReservationDetailModal
        detail={detailModal.detail}
        isOpen={detailModal.isOpen}
        onClose={() => setDetailModal({ isOpen: false, detail: null })}
      />
    </div>
  );
}