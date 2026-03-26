'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type {
    BusinessReservationItem,
    PaginationInfo,
} from '@/types/business/reservation';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';
import { ReservationFilterToolbar } from '@/components/business/reservations/reservation-filter-toolbar';
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

    // 액션 핸들러 — 성공 시 해당 예약의 status를 낙관적으로 갱신
    const handleApprove = async (reservationId: string) => {
        const result = await businessReservationService.approveReservation(
            businessId,
            reservationId
        );

        if (!result.success) {
            toast.error(result.message || '승인에 실패했습니다.');
            return;
        }

        setReservations(prev =>
            prev.map(r =>
                r.reservationId === reservationId
                    ? { ...r, status: 'CONFIRMED' as const, requiresAction: false }
                    : r
            )
        );
        toast.success('예약을 승인했습니다.');
    };

    const handleReject = async (reservationId: string) => {
        const result = await businessReservationService.rejectReservation(
            businessId,
            reservationId
        );

        if (!result.success) {
            toast.error(result.message || '거절에 실패했습니다.');
            return;
        }

        setReservations(prev =>
            prev.map(r =>
                r.reservationId === reservationId
                    ? { ...r, status: 'REJECTED' as const, requiresAction: false }
                    : r
            )
        );
        toast.success('예약을 거절했습니다.');
    };

    const handleComplete = async (reservationId: string) => {
        const result = await businessReservationService.completeReservation(
            businessId,
            reservationId
        );

        if (!result.success) {
            toast.error(result.message || '완료 처리에 실패했습니다.');
            return;
        }

        setReservations(prev =>
            prev.map(r =>
                r.reservationId === reservationId
                    ? { ...r, status: 'COMPLETED' as const, requiresAction: false }
                    : r
            )
        );
        toast.success('예약을 완료 처리했습니다.');
    };

    const handleNoShow = async (reservationId: string) => {
        const result = await businessReservationService.noShowReservation(
            businessId,
            reservationId
        );

        if (!result.success) {
            toast.error(result.message || '노쇼 처리에 실패했습니다.');
            return;
        }

        setReservations(prev =>
            prev.map(r =>
                r.reservationId === reservationId
                    ? { ...r, status: 'NO_SHOW' as const, requiresAction: false }
                    : r
            )
        );
        toast.success('노쇼 처리했습니다.');
    };

    return (
        <div className="space-y-6">
            <ReservationFilterToolbar />

            {/* TODO(step6): ReservationStatsCards — BusinessReservationItem 타입으로 교체 필요 */}
            <ReservationStatsCards reservations={reservations as any} />

            <Card>
                <CardContent className="pt-4">
                    <Table>
                        <ReservationTableHeader />
                        {/* TODO(step6): ReservationTableBody — BusinessReservationItem 타입으로 교체 필요 */}
                        <ReservationTableBody
                            reservations={reservations as any}
                            onApprove={handleApprove}
                            onReject={handleReject}
                            onComplete={handleComplete}
                            onNoShow={handleNoShow}
                        />
                    </Table>
                </CardContent>
            </Card>
        </div>
    );
}