import type { ReservationStatus } from '@/types/business/reservation';

// 예약 상태 한국어 표기
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

// 캘린더 월 뷰 이벤트 색상
export const RESERVATION_STATUS_COLOR: Record<
  ReservationStatus,
  { bg: string; text: string }
> = {
  CONFIRMED: { bg: 'bg-teal-100',  text: 'text-teal-700'  },
  COMPLETED: { bg: 'bg-gray-100',  text: 'text-gray-500'  },
  PENDING:   { bg: 'bg-amber-100', text: 'text-amber-700' },
  CANCELLED: { bg: 'bg-red-100',   text: 'text-red-600'   },
  REJECTED:  { bg: 'bg-red-100',   text: 'text-red-600'   },
  NO_SHOW:   { bg: 'bg-rose-100',  text: 'text-rose-700'  },
};

// 캘린더 상태 필터 버튼 + FullCalendar 이벤트 색상
export const RESERVATION_STATUS_FILTERS: {
  value: ReservationStatus;
  label: string;
  activeClass: string;
  eventColor: string;
  eventTextColor: string;
}[] = [
  { value: 'CONFIRMED', label: '예약확정', activeClass: 'bg-teal-100 text-teal-700 border-teal-200',   eventColor: '#ccfbf1', eventTextColor: '#0f766e' },
  { value: 'COMPLETED', label: '완료',     activeClass: 'bg-gray-100 text-gray-600 border-gray-200',   eventColor: '#f3f4f6', eventTextColor: '#6b7280' },
  { value: 'PENDING',   label: '승인대기', activeClass: 'bg-amber-100 text-amber-700 border-amber-200', eventColor: '#fef3c7', eventTextColor: '#92400e' },
  { value: 'CANCELLED', label: '취소',     activeClass: 'bg-red-100 text-red-600 border-red-200',       eventColor: '#fee2e2', eventTextColor: '#991b1b' },
  { value: 'NO_SHOW',   label: '노쇼',     activeClass: 'bg-rose-100 text-rose-700 border-rose-200',    eventColor: '#ffe4e6', eventTextColor: '#9f1239' },
];