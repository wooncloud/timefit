import { TableCell, TableRow } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { ReservationActionsDropdown } from './reservation-actions-dropdown';

export interface Reservation {
  id: string;
  reservationNumber: string;
  dateTime: string;
  customerName: string;
  customerPhone: string;
  service: string;
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled' | 'noshow';
}

interface ReservationTableRowProps {
  reservation: Reservation;
}

const getStatusBadgeVariant = (status: Reservation['status']) => {
  switch (status) {
    case 'confirmed':
      return 'default' as const;
    case 'pending':
      return 'secondary' as const;
    case 'completed':
      return 'outline' as const;
    case 'cancelled':
    case 'noshow':
      return 'destructive' as const;
    default:
      return 'outline' as const;
  }
};

const getStatusLabel = (status: Reservation['status']) => {
  switch (status) {
    case 'pending':
      return '승인대기';
    case 'confirmed':
      return '예약확정';
    case 'completed':
      return '완료';
    case 'cancelled':
      return '취소';
    case 'noshow':
      return '노쇼';
    default:
      return status;
  }
};

const formatDateTime = (dateTime: string) => {
  const date = new Date(dateTime);
  const dateStr = date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
  const timeStr = date.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
  return `${dateStr} ${timeStr}`;
};

export function ReservationTableRow({ reservation }: ReservationTableRowProps) {
  return (
    <TableRow>
      <TableCell className="font-medium">
        {reservation.reservationNumber}
      </TableCell>
      <TableCell>{formatDateTime(reservation.dateTime)}</TableCell>
      <TableCell>
        <div className="flex flex-col">
          <span className="font-medium">{reservation.customerName}</span>
          <span className="text-sm text-muted-foreground">
            {reservation.customerPhone}
          </span>
        </div>
      </TableCell>
      <TableCell>{reservation.service}</TableCell>
      <TableCell>
        <Badge variant={getStatusBadgeVariant(reservation.status)}>
          {getStatusLabel(reservation.status)}
        </Badge>
      </TableCell>
      <TableCell className="text-right">
        <ReservationActionsDropdown reservationId={reservation.id} />
      </TableCell>
    </TableRow>
  );
}
