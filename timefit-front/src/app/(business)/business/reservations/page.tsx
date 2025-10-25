'use client';

import { Card, CardContent } from '@/components/ui/card';
import { Table } from '@/components/ui/table';
import { ReservationTableHeader } from '@/components/business/reservations/reservation-table-header';
import { ReservationTableBody } from '@/components/business/reservations/reservation-table-body';
import { ReservationFilterToolbar } from '@/components/business/reservations/reservation-filter-toolbar';
import { ReservationStatsCards } from '@/components/business/reservations/reservation-stats-cards';
import { mockReservations } from '@/lib/mock';

export default function Page() {
  return (
    <div className="space-y-6">
      <ReservationFilterToolbar />

      <ReservationStatsCards reservations={mockReservations} />

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
