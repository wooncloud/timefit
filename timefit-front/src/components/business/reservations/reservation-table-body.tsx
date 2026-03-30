import { TableBody } from '@/components/ui/table';

import type { BusinessReservationItem } from '@/types/business/reservation';
import { ReservationTableRow } from './reservation-table-row';

interface ReservationTableBodyProps {
  reservations: BusinessReservationItem[];
  onDetail: (id: string) => void;
  onApprove: (id: string) => void;
  onReject: (id: string) => void;
  onComplete: (id: string) => void;
  onNoShow: (id: string) => void;
}

export function ReservationTableBody({
                                       reservations,
                                       onDetail,
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
          onDetail={onDetail}
          onApprove={onApprove}
          onReject={onReject}
          onComplete={onComplete}
          onNoShow={onNoShow}
        />
      ))}
    </TableBody>
  );
}