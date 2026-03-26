'use client';

import type { BusinessReservationItem, ReservationStatus } from '@/types/business/reservation';
import { Badge } from '@/components/ui/badge';
import { TableCell, TableRow } from '@/components/ui/table';

import { ReservationActionsDropdown } from './reservation-actions-dropdown';

interface ReservationTableRowProps {
  reservation: BusinessReservationItem;
  onApprove: (id: string) => void;
  onReject: (id: string) => void;
  onComplete: (id: string) => void;
  onNoShow: (id: string) => void;
}

const getStatusBadgeVariant = (status: ReservationStatus) => {
  switch (status) {
    case 'CONFIRMED':
      return 'default' as const;
    case 'PENDING':
      return 'secondary' as const;
    case 'COMPLETED':
      return 'outline' as const;
    case 'CANCELLED':
    case 'REJECTED':
    case 'NO_SHOW':
      return 'destructive' as const;
    default:
      return 'outline' as const;
  }
};

const getStatusLabel = (status: ReservationStatus) => {
  switch (status) {
    case 'PENDING':
      return '승인대기';
    case 'CONFIRMED':
      return '예약확정';
    case 'COMPLETED':
      return '완료';
    case 'CANCELLED':
      return '취소';
    case 'REJECTED':
      return '거절';
    case 'NO_SHOW':
      return '노쇼';
    default:
      return status;
  }
};

const formatDateTime = (date: string, time: string) => {
  const dateObj = new Date(`${date}T${time}`);
  const dateStr = dateObj.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
  const timeStr = dateObj.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
  return `${dateStr} ${timeStr}`;
};

export function ReservationTableRow({
                                      reservation,
                                      onApprove,
                                      onReject,
                                      onComplete,
                                      onNoShow,
                                    }: ReservationTableRowProps) {
  return (
    <TableRow>
      <TableCell className="font-medium">
        {reservation.reservationNumber}
      </TableCell>
      <TableCell>
        {formatDateTime(reservation.reservationDate, reservation.reservationTime)}
      </TableCell>
      <TableCell>
        <div className="flex flex-col">
          <span className="font-medium">{reservation.customerName}</span>
          <span className="text-sm text-muted-foreground">
            {reservation.customerPhone}
          </span>
        </div>
      </TableCell>
      <TableCell>{reservation.reservationDuration}분</TableCell>
      <TableCell>
        <Badge variant={getStatusBadgeVariant(reservation.status)}>
          {getStatusLabel(reservation.status)}
        </Badge>
      </TableCell>
      <TableCell className="text-right">
        <ReservationActionsDropdown
          reservationId={reservation.reservationId}
          status={reservation.status}
          onApprove={onApprove}
          onReject={onReject}
          onComplete={onComplete}
          onNoShow={onNoShow}
        />
      </TableCell>
    </TableRow>
  );
}