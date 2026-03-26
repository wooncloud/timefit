'use client';

import { Check, Clock, Star, X } from 'lucide-react';

import type { ReservationStats } from '@/types/business/reservation';

import { ReservationStatCard } from './reservation-stat-card';

interface ReservationStatsCardsProps {
  stats: ReservationStats;
}

export function ReservationStatsCards({ stats }: ReservationStatsCardsProps) {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
      <ReservationStatCard
        icon={Clock}
        count={stats.pending}
        label="승인대기"
        colorClass={{
          bg: 'bg-yellow-100 dark:bg-yellow-900',
          icon: 'text-yellow-600 dark:text-yellow-400',
        }}
      />
      <ReservationStatCard
        icon={Check}
        count={stats.confirmed}
        label="예약확정"
        colorClass={{
          bg: 'bg-green-100 dark:bg-green-900',
          icon: 'text-green-600 dark:text-green-400',
        }}
      />
      <ReservationStatCard
        icon={Star}
        count={stats.completed}
        label="완료"
        colorClass={{
          bg: 'bg-blue-100 dark:bg-blue-900',
          icon: 'text-blue-600 dark:text-blue-400',
        }}
      />
      <ReservationStatCard
        icon={X}
        count={stats.cancelled + stats.noShow}
        label="취소/노쇼"
        colorClass={{
          bg: 'bg-red-100 dark:bg-red-900',
          icon: 'text-red-600 dark:text-red-400',
        }}
      />
    </div>
  );
}