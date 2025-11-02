import type { BusinessHours } from '@/lib/data/schedule/weekdays';

export const mockBusinessHours: BusinessHours[] = [
  { id: 'mon', startTime: '09:00', endTime: '18:00', isEnabled: true },
  { id: 'tue', startTime: '09:00', endTime: '18:00', isEnabled: true },
  { id: 'wed', startTime: '09:00', endTime: '18:00', isEnabled: true },
  { id: 'thu', startTime: '09:00', endTime: '18:00', isEnabled: true },
  { id: 'fri', startTime: '09:00', endTime: '18:00', isEnabled: true },
  { id: 'sat', startTime: '10:00', endTime: '15:00', isEnabled: false },
  { id: 'sun', startTime: '10:00', endTime: '15:00', isEnabled: false },
];
