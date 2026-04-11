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
  const { allReservations, fetchReservations } = useCalendarData(businessId, initialReservations);
  const { activeStatuses, toggleStatus } = useCalendarFilter();
  const { detailModal, handleEventClick, closeDetailModal } = useCalendarModal(businessId);
  const {
    calendarRef,
    currentView,
    currentDate,
    yearViewYear,
    titleText,
    handleDatesSet,
    handleNavigate,
    handleToday,
    handleViewChange,
    handleMonthClick,
    handleDayClick,
    handleMoreClick,
  } = useCalendarView({ onFetchReservations: fetchReservations });
  const { containerRef, containerStyle } = useCalendarScale(currentView);

  const fcEvents = toCalendarEvents(allReservations, activeStatuses);

  return {
    // refs
    calendarRef,
    containerRef,
    // 상태
    currentView,
    currentDate,
    yearViewYear,
    titleText,
    activeStatuses,
    fcEvents,
    detailModal,
    containerStyle,
    // 핸들러
    toggleStatus,
    handleDatesSet,
    handleNavigate,
    handleToday,
    handleViewChange,
    handleMonthClick,
    handleDayClick,
    handleMoreClick,
    handleEventClick,
    closeDetailModal,
  };
}