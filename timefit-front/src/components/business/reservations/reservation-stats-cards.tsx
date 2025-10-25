'use client';

import type { Reservation } from './reservation-table-row';
import { ReservationStatCard } from './reservation-stat-card';
import { reservationStatConfigs } from '@/lib/data/reservation/reservationStatConfigs';

interface ReservationStatsCardsProps {
  reservations: Reservation[];
}

export function ReservationStatsCards({
  reservations,
}: ReservationStatsCardsProps) {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
      {reservationStatConfigs.map((config) => (
        <ReservationStatCard
          key={config.id}
          icon={config.icon}
          count={config.getCount(reservations)}
          label={config.label}
          colorClass={config.colorClass}
        />
      ))}
    </div>
  );
}
