'use client';

import type {
  BusinessReservationItem,
  PaginationInfo,
  ReservationStats,
} from '@/types/business/reservation';
import { useReservations } from '@/hooks/business/use-reservations';
import {
  ReservationFilterToolbar,
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
  const {
    reservations,
    pagination,
    stats,
    isLoading,
    detailModal,
    handleDetail,
    closeDetailModal,
    handleSearch,
    handlePageChange,
    handleApprove,
    handleReject,
    handleComplete,
    handleNoShow,
  } = useReservations({ initialReservations, initialPagination, initialStats, businessId });

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
        onClose={closeDetailModal}
      />
    </div>
  );
}