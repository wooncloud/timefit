import type { BusinessReservationItem } from '@/types/business/reservation';
import { toCalendarEvents } from '@/lib/data/calendar/calendar-event-mapper';
import { useCalendarData } from './use-calendar-data';
import { useCalendarFilter } from './use-calendar-filter';
import { useCalendarModal } from './use-calendar-modal';
import { useCalendarScale } from './use-calendar-scale';
import { useCalendarView } from './use-calendar-view';

interface UseCalendarProps {
  initialReservations: BusinessReservationItem[];
  businessId: string;
}

/**
 * 캘린더 Facade hook
 * 각 역할별 sub-hook을 조합하여 calendar-client.tsx에 단일 진입점 제공
 */
export function useCalendar({ initialReservations, businessId }: UseCalendarProps) {
  const data = useCalendarData(businessId, initialReservations);
  const filter = useCalendarFilter();
  const modal = useCalendarModal(businessId);
  const view = useCalendarView({ onFetchReservations: data.fetchReservations });
  const scale = useCalendarScale(view.currentView);

  const fcEvents = toCalendarEvents(data.allReservations, filter.activeStatuses);

  return { view, scale, filter, modal, data, fcEvents };
}