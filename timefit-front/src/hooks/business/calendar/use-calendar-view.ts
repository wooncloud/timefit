import { useCallback, useRef, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import type { DatesSetArg } from '@fullcalendar/core';
import dayjs from 'dayjs';

import type { ViewType } from '@/types/business/calendar';

interface UseCalendarViewProps {
  onFetchReservations: (start: string, end: string) => void;
}

/**
 * 캘린더 뷰 전환 및 날짜 이동 담당
 * calendarRef(FullCalendar API), currentView, currentDate, yearViewYear 상태 관리
 */
export function useCalendarView({ onFetchReservations }: UseCalendarViewProps) {
  const calendarRef = useRef<FullCalendar>(null);
  const [currentView, setCurrentView] = useState<ViewType>('month');
  const [currentDate, setCurrentDate] = useState(dayjs());
  const [yearViewYear, setYearViewYear] = useState(dayjs().year());

  const titleText = (() => {
    if (currentView === 'year') return `${yearViewYear}년`;
    if (currentView === 'month') return `${currentDate.year()}년 ${currentDate.month() + 1}월`;
    if (currentView === 'timeGridDay') return `${currentDate.year()}년 ${currentDate.month() + 1}월 ${currentDate.date()}일`;
    return calendarRef.current?.getApi().view.title ?? '';
  })();

  const handleDatesSet = useCallback((arg: DatesSetArg) => {
    onFetchReservations(
      dayjs(arg.start).format('YYYY-MM-DD'),
      dayjs(arg.end).subtract(1, 'day').format('YYYY-MM-DD')
    );
  }, [onFetchReservations]);

  const handleNavigate = (dir: number) => {
    if (currentView === 'year') { setYearViewYear(y => y + dir); return; }
    if (currentView === 'month') {
      const next = currentDate.add(dir, 'month');
      setCurrentDate(next);
      onFetchReservations(next.startOf('month').format('YYYY-MM-DD'), next.endOf('month').format('YYYY-MM-DD'));
      return;
    }
    if (currentView === 'timeGridDay') {
      const next = currentDate.add(dir, 'day');
      setCurrentDate(next);
      calendarRef.current?.getApi().gotoDate(next.toDate());
      return;
    }
    if (dir === 1) calendarRef.current?.getApi().next();
    else calendarRef.current?.getApi().prev();
  };

  const handleToday = () => {
    const today = dayjs();
    if (currentView === 'year') { setYearViewYear(today.year()); return; }
    if (currentView === 'month') {
      setCurrentDate(today);
      onFetchReservations(today.startOf('month').format('YYYY-MM-DD'), today.endOf('month').format('YYYY-MM-DD'));
      return;
    }
    setCurrentDate(today);
    calendarRef.current?.getApi().today();
  };

  const handleViewChange = (view: ViewType) => {
    setCurrentView(view);
    if (view === 'year' || view === 'month') return;
    calendarRef.current?.getApi().changeView(view);
  };

  const handleMonthClick = (year: number, month: number) => {
    const date = dayjs(new Date(year, month, 1));
    setCurrentDate(date);
    setCurrentView('month');
    onFetchReservations(date.startOf('month').format('YYYY-MM-DD'), date.endOf('month').format('YYYY-MM-DD'));
  };

  const handleDayClick = (year: number, month: number, day: number) => {
    const date = dayjs(new Date(year, month, day));
    setCurrentDate(date);
    setCurrentView('timeGridWeek');
    calendarRef.current?.getApi().changeView('timeGridWeek');
    calendarRef.current?.getApi().gotoDate(date.toDate());
  };

  const handleMoreClick = (dateStr: string) => {
    const date = dayjs(dateStr);
    setCurrentDate(date);
    setCurrentView('timeGridDay');
    calendarRef.current?.getApi().changeView('timeGridDay');
    calendarRef.current?.getApi().gotoDate(date.toDate());
  };

  return {
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
  };
}