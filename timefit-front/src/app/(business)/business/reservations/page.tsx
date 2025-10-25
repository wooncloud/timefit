'use client';

import { Card, CardContent } from '@/components/ui/card';
import { Table } from '@/components/ui/table';
import { ReservationTableHeader } from '@/components/business/reservations/reservation-table-header';
import { ReservationTableBody } from '@/components/business/reservations/reservation-table-body';
import { mockReservations } from '@/lib/mock';

export default function Page() {
  return (
    <div>
      <Card>
        <CardContent className="pt-4">
          <Table>
            <ReservationTableHeader />
            <ReservationTableBody reservations={mockReservations} />
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
