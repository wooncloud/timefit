import { Check, Clock, LucideIcon, Star, X } from 'lucide-react';

import type { BusinessReservationItem } from '@/types/business/reservation';

interface ReservationStatConfig {
  id: string;
  icon: LucideIcon;
  label: string;
  colorClass: {
    bg: string;
    icon: string;
  };
  getCount: (reservations: BusinessReservationItem[]) => number;
}

export const reservationStatConfigs: ReservationStatConfig[] = [
  {
    id: 'pending',
    icon: Clock,
    label: '승인대기',
    colorClass: {
      bg: 'bg-yellow-100 dark:bg-yellow-900',
      icon: 'text-yellow-600 dark:text-yellow-400',
    },
    getCount: reservations =>
      reservations.filter(r => r.status === 'PENDING').length,
  },
  {
    id: 'confirmed',
    icon: Check,
    label: '예약확정',
    colorClass: {
      bg: 'bg-green-100 dark:bg-green-900',
      icon: 'text-green-600 dark:text-green-400',
    },
    getCount: reservations =>
      reservations.filter(r => r.status === 'CONFIRMED').length,
  },
  {
    id: 'completed',
    icon: Star,
    label: '완료',
    colorClass: {
      bg: 'bg-blue-100 dark:bg-blue-900',
      icon: 'text-blue-600 dark:text-blue-400',
    },
    getCount: reservations =>
      reservations.filter(r => r.status === 'COMPLETED').length,
  },
  {
    id: 'cancelled',
    icon: X,
    label: '취소/노쇼',
    colorClass: {
      bg: 'bg-red-100 dark:bg-red-900',
      icon: 'text-red-600 dark:text-red-400',
    },
    getCount: reservations =>
      reservations.filter(r => r.status === 'CANCELLED').length +
      reservations.filter(r => r.status === 'NO_SHOW').length,
  },
];