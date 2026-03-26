'use client';

import { Check, Clock, LucideIcon, Star, X } from 'lucide-react';

import type { BusinessReservationItem } from '@/types/business/reservation';
import { ReservationStatCard } from './reservation-stat-card';

interface StatConfig {
  id: string;
  icon: LucideIcon;
  label: string;
  colorClass: { bg: string; icon: string };
  getCount: (reservations: BusinessReservationItem[]) => number;
}

const statConfigs: StatConfig[] = [
  {
    id: 'pending',
    icon: Clock,
    label: '승인대기',
    colorClass: {
      bg: 'bg-yellow-100 dark:bg-yellow-900',
      icon: 'text-yellow-600 dark:text-yellow-400',
    },
    getCount: r => r.filter(v => v.status === 'PENDING').length,
  },
  {
    id: 'confirmed',
    icon: Check,
    label: '예약확정',
    colorClass: {
      bg: 'bg-green-100 dark:bg-green-900',
      icon: 'text-green-600 dark:text-green-400',
    },
    getCount: r => r.filter(v => v.status === 'CONFIRMED').length,
  },
  {
    id: 'completed',
    icon: Star,
    label: '완료',
    colorClass: {
      bg: 'bg-blue-100 dark:bg-blue-900',
      icon: 'text-blue-600 dark:text-blue-400',
    },
    getCount: r => r.filter(v => v.status === 'COMPLETED').length,
  },
  {
    id: 'cancelled',
    icon: X,
    label: '취소/노쇼',
    colorClass: {
      bg: 'bg-red-100 dark:bg-red-900',
      icon: 'text-red-600 dark:text-red-400',
    },
    getCount: r =>
      r.filter(v => v.status === 'CANCELLED' || v.status === 'NO_SHOW').length,
  },
];

interface ReservationStatsCardsProps {
  reservations: BusinessReservationItem[];
}

export function ReservationStatsCards({ reservations }: ReservationStatsCardsProps) {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
      {statConfigs.map(config => (
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