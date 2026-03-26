'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type {
  BusinessReservationItem,
  PaginationInfo,
} from '@/types/business/reservation';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';
import {
  ReservationFilterToolbar,
  type FilterValues,
} from '@/components/business/reservations/reservation-filter-toolbar';
import { ReservationPagination } from '@/components/business/reservations/reservation-pagination';
import { ReservationStatsCards } from '@/components/business/reservations/reservation-stats-cards';
import { ReservationTableBody } from '@/components/business/reservations/reservation-table-body';
import { ReservationTableHeader } from '@/components/business/reservations/reservation-table-header';
import { Card, CardContent } from '@/components/ui/card';
import { Table } from '@/components/ui/table';

interface ReservationsClientProps {
  initialReservations: BusinessReservationItem[];
  initialPagination: PaginationInfo;
  businessId: string;
}

export function ReservationsClient({
                                     initialReservations,
                                     initialPagination,
                                     businessId,
                                   }: ReservationsClientProps) {
  const [reservations, setReservations] = useState<BusinessReservationItem[]>(initialReservations);
  const [pagination, setPagination] = useState<PaginationInfo>(initialPagination);
  const [isLoading, setIsLoading] = useState(false);
  const [currentFilters, setCurrentFilters] = useState<FilterValues>({});

  // 공통 fetch 함수
  const fetchReservations = async (filters: FilterValues, page: number) => {
    const params = new URLSearchParams();
    if (filters.status) params.append('status', filters.status);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);
    if (filters.customerName) params.append('customerName', filters.customerName);
    params.append('page', page.toString());
    params.append('size', '20');

    const response = await fetch(
        `/api/business/${businessId}/reservations?${params.toString()}`
    );
    return response.json();
  };

  // 필터 검색
  const handleSearch = async (filters: FilterValues) => {
    try {
      setIsLoading(true);
      const result = await fetchReservations(filters, 0);
      if (!result.success) { toast.error('조회에 실패했습니다.'); return; }
      setCurrentFilters(filters);
      setReservations(result.data.reservations);
      setPagination(result.data.pagination);
    } catch {
      toast.error('조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 페이지 이동 (현재 필터 유지)
  const handlePageChange = async (page: number) => {
    try {
      setIsLoading(true);
      const result = await fetchReservations(currentFilters, page);
      if (!result.success) { toast.error('페이지 로드에 실패했습니다.'); return; }
      setReservations(result.data.reservations);
      setPagination(result.data.pagination);
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
  };

  const handleReject = async (reservationId: string) => {
    const result = await businessReservationService.rejectReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '거절에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
        r.reservationId === reservationId ? { ...r, status: 'REJECTED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 거절했습니다.');
  };

  const handleComplete = async (reservationId: string) => {
    const result = await businessReservationService.completeReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '완료 처리에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
        r.reservationId === reservationId ? { ...r, status: 'COMPLETED' as const, requiresAction: false } : r
    ));
    toast.success('예약을 완료 처리했습니다.');
  };

  const handleNoShow = async (reservationId: string) => {
    const result = await businessReservationService.noShowReservation(businessId, reservationId);
    if (!result.success) { toast.error(result.message || '노쇼 처리에 실패했습니다.'); return; }
    setReservations(prev => prev.map(r =>
        r.reservationId === reservationId ? { ...r, status: 'NO_SHOW' as const, requiresAction: false } : r
    ));
    toast.success('노쇼 처리했습니다.');
  };

  return (
      <div className="space-y-6">
        <ReservationFilterToolbar onSearch={handleSearch} />

        <ReservationStatsCards reservations={reservations} />

        <Card>
          <CardContent className="pt-4">
            <Table>
              <ReservationTableHeader />
              <ReservationTableBody
                  reservations={reservations}
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
      </div>
  );
}