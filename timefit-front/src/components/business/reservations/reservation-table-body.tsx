import { TableBody } from '@/components/ui/table';

import { ReservationTableRow, type Reservation } from './reservation-table-row';

interface ReservationTableBodyProps {
  reservations: Reservation[];
}

export function ReservationTableBody({
  reservations,
}: ReservationTableBodyProps) {
  return (
    <TableBody>
      {reservations.map(reservation => (
        <ReservationTableRow key={reservation.id} reservation={reservation} />
      ))}
    </TableBody>
  );
}
