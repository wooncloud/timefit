export type TimeSlotStatus =
  | 'available'
  | 'lunch'
  | 'dinner'
  | 'break'
  | 'unavailable';

export interface TimeSlotStatusConfig {
  label: string;
  className: string;
  menuLabel: string;
}

export const timeSlotStatusConfig: Record<
  TimeSlotStatus,
  TimeSlotStatusConfig
> = {
  available: {
    label: '',
    className: 'bg-background hover:bg-accent',
    menuLabel: '예약 가능',
  },
  lunch: {
    label: '점심',
    className: 'bg-orange-100 hover:bg-orange-200 dark:bg-orange-900',
    menuLabel: '점심시간',
  },
  dinner: {
    label: '저녁',
    className: 'bg-purple-100 hover:bg-purple-200 dark:bg-purple-900',
    menuLabel: '저녁시간',
  },
  break: {
    label: '휴게',
    className: 'bg-blue-100 hover:bg-blue-200 dark:bg-blue-900',
    menuLabel: '휴게시간',
  },
  unavailable: {
    label: '불가',
    className: 'bg-gray-100 hover:bg-gray-200 dark:bg-gray-800',
    menuLabel: '예약 불가',
  },
};

export const timeSlotStatuses: TimeSlotStatus[] = [
  'available',
  'lunch',
  'dinner',
  'break',
  'unavailable',
];
