import type { BusinessReservationItem } from '@/types/business/reservation';
import { TableBody } from '@/components/ui/table';

import { ReservationTableRow } from './reservation-table-row';

interface ReservationTableBodyProps {
  reservations: BusinessReservationItem[];
  onApprove: (id: string) => void;
  onReject: (id: string) => void;
  onComplete: (id: string) => void;
  onNoShow: (id: string) => void;
}

export function ReservationTableBody({
                                       reservations,
                                       onApprove,
                                       onReject,
                                       onComplete,
                                       onNoShow,
                                     }: ReservationTableBodyProps) {
  return (
    <TableBody>
      {reservations.map(reservation => (
        <ReservationTableRow
          key={reservation.reservationId}
          reservation={reservation}
          onApprove={onApprove}
          onReject={onReject}
          onComplete={onComplete}
          onNoShow={onNoShow}
        />
      ))}
    </TableBody>
  );
}