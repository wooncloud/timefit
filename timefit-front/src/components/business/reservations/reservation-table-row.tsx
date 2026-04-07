'use client';

import dayjs from 'dayjs';

import type { BusinessReservationItem } from '@/types/business/reservation';
import { Badge } from '@/components/ui/badge';
import { TableCell, TableRow } from '@/components/ui/table';

import { ReservationActionsDropdown } from './reservation-actions-dropdown';
import {
  RESERVATION_STATUS_LABEL,
  RESERVATION_STATUS_VARIANT,
} from '@/lib/constants/reservation-status';

interface ReservationTableRowProps {
  reservation: BusinessReservationItem;
  onDetail: (id: string) => void;
  onApprove: (id: string) => void;
  onReject: (id: string) => void;
  onComplete: (id: string) => void;
  onNoShow: (id: string) => void;
}

export function ReservationTableRow({
                                      reservation,
                                      onDetail,
                                      onApprove,
                                      onReject,
                                      onComplete,
                                      onNoShow,
                                    }: ReservationTableRowProps) {
  const dateTime = `${dayjs(reservation.reservationDate).format('YYYY.MM.DD')} ${reservation.reservationTime.slice(0, 5)}`;

  return (
    <TableRow>
      <TableCell className="font-medium">{reservation.reservationNumber}</TableCell>
      <TableCell>{dateTime}</TableCell>
      <TableCell>
        <div className="flex flex-col">
          <span className="font-medium">{reservation.customerName}</span>
          <span className="text-sm text-muted-foreground">{reservation.customerPhone}</span>
        </div>
      </TableCell>
      <TableCell>{reservation.reservationDuration}분</TableCell>
      <TableCell>
        <Badge variant={RESERVATION_STATUS_VARIANT[reservation.status]}>
          {RESERVATION_STATUS_LABEL[reservation.status]}
        </Badge>
      </TableCell>
      <TableCell className="text-right">
        <ReservationActionsDropdown
          reservationId={reservation.reservationId}
          status={reservation.status}
          onDetail={onDetail}
          onApprove={onApprove}
          onReject={onReject}
          onComplete={onComplete}
          onNoShow={onNoShow}
        />
      </TableCell>
    </TableRow>
  );
}