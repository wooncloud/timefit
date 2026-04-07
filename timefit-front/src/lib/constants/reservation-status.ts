import type { ReservationStatus } from '@/types/business/reservation';

export const RESERVATION_STATUS_LABEL: Record<ReservationStatus, string> = {
  PENDING: '승인대기',
  CONFIRMED: '예약확정',
  COMPLETED: '완료',
  CANCELLED: '취소',
  REJECTED: '거절',
  NO_SHOW: '노쇼',
};

export const RESERVATION_STATUS_VARIANT: Record<
  ReservationStatus,
  'default' | 'secondary' | 'outline' | 'destructive'
> = {
  CONFIRMED: 'default',
  PENDING: 'secondary',
  COMPLETED: 'outline',
  CANCELLED: 'destructive',
  REJECTED: 'destructive',
  NO_SHOW: 'destructive',
};