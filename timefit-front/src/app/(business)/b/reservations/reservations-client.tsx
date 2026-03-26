'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type {
  BusinessReservationItem,
  PaginationInfo,
} from '@/types/business/reservation';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';
import { ReservationFilterToolbar } from '@/components/business/reservations/reservation-filter-toolbar';
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

  const handlePageChange = async (page: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `/api/business/${businessId}/reservations?page=${page}&size=20`
      );
      const result = await response.json();

      if (!result.success) {
        toast.error('페이지 로드에 실패했습니다.');
        return;
      }

      setReservations(result.data.reservations);
      setPagination(result.data.pagination);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch {
      toast.error('페이지 로드 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

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
      <ReservationFilterToolbar />

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