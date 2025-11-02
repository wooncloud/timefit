export const WEEKDAYS = [
  { id: 'mon', label: '월', fullLabel: '월요일' },
  { id: 'tue', label: '화', fullLabel: '화요일' },
  { id: 'wed', label: '수', fullLabel: '수요일' },
  { id: 'thu', label: '목', fullLabel: '목요일' },
  { id: 'fri', label: '금', fullLabel: '금요일' },
  { id: 'sat', label: '토', fullLabel: '토요일' },
  { id: 'sun', label: '일', fullLabel: '일요일' },
] as const;

export type WeekdayId = (typeof WEEKDAYS)[number]['id'];

export interface BusinessHours {
  id: WeekdayId;
  startTime: string;
  endTime: string;
  isEnabled: boolean;
}
