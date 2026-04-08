'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import koLocale from '@fullcalendar/core/locales/ko';
import type { EventClickArg, DatesSetArg, EventInput } from '@fullcalendar/core';
import dayjs from 'dayjs';
import { toast } from 'sonner';

import type { BusinessReservationItem, BusinessReservationDetail } from '@/types/business/reservation';
import type { ViewType } from '@/types/business/calendar';
import { businessReservationService } from '@/services/reservation/reservation-business-service.client';
import { ReservationDetailModal } from '@/components/business/reservations/reservation-detail-modal';
import { CalendarMonthView } from '@/components/business/calendar/calendar-month-view';
import { CalendarYearView } from '@/components/business/calendar/calendar-year-view';
import { Button } from '@/components/ui/button';
import { CALENDAR_MIN_HEIGHT, VIEW_LABELS } from '@/lib/constants/calendar';
import { RESERVATION_STATUS_FILTERS } from '@/lib/constants/reservation-status';

import '@/styles/fullcalendar-override.css';

interface CalendarClientProps {
  initialReservations: BusinessReservationItem[];
  businessId: string;
}

export function CalendarClient({ initialReservations, businessId }: CalendarClientProps) {
  const calendarRef = useRef<FullCalendar>(null);
  const [currentView, setCurrentView] = useState<ViewType>('month');
  const [currentDate, setCurrentDate] = useState(dayjs());
  const [yearViewYear, setYearViewYear] = useState(dayjs().year());
  const [allReservations, setAllReservations] = useState<BusinessReservationItem[]>(initialReservations);
  const [activeStatuses, setActiveStatuses] = useState<Set<string>>(new Set(['CONFIRMED', 'COMPLETED']));
  const [detailModal, setDetailModal] = useState<{ isOpen: boolean; detail: BusinessReservationDetail | null }>(
    { isOpen: false, detail: null }
  );

  // 510px 이하에서 비율 축소
  const containerRef = useRef<HTMLDivElement>(null);
  const [scale, setScale] = useState(1);
  const SCALE_BREAKPOINT = 510;

  useEffect(() => {
    const handleResize = () => {
      const width = window.innerWidth - 256; // 사이드바 너비 제외 (대략)
      if (width < SCALE_BREAKPOINT) {
        setScale(Math.max(0.6, width / SCALE_BREAKPOINT));
      } else {
        setScale(1);
      }
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const titleText = (() => {
    if (currentView === 'year') return `${yearViewYear}년`;
    if (currentView === 'month') return `${currentDate.year()}년 ${currentDate.month() + 1}월`;
    if (currentView === 'timeGridDay') return `${currentDate.year()}년 ${currentDate.month() + 1}월 ${currentDate.date()}일`;
    return calendarRef.current?.getApi().view.title ?? '';
  })();

  const toggleStatus = (status: string) => {
    setActiveStatuses(prev => {
      const next = new Set(prev);
      if (next.has(status)) { if (next.size === 1) return prev; next.delete(status); }
      else next.add(status);
      return next;
    });
  };

  const fetchReservations = useCallback(async (start: string, end: string) => {
    try {
      const params = new URLSearchParams();
      params.append('startDate', start);
      params.append('endDate', end);
      params.append('size', '100');
      const res = await fetch(`/api/business/${businessId}/reservations?${params.toString()}`);
      const result = await res.json();
      if (result.success && result.data) setAllReservations(result.data.reservations);
    } catch { toast.error('예약 데이터를 불러오는 데 실패했습니다.'); }
  }, [businessId]);

  const handleDatesSet = useCallback((arg: DatesSetArg) => {
    fetchReservations(
      dayjs(arg.start).format('YYYY-MM-DD'),
      dayjs(arg.end).subtract(1, 'day').format('YYYY-MM-DD')
    );
  }, [fetchReservations]);

  const handleNavigate = (dir: number) => {
    if (currentView === 'year') { setYearViewYear(y => y + dir); return; }
    if (currentView === 'month') {
      const next = currentDate.add(dir, 'month');
      setCurrentDate(next);
      fetchReservations(next.startOf('month').format('YYYY-MM-DD'), next.endOf('month').format('YYYY-MM-DD'));
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
      fetchReservations(today.startOf('month').format('YYYY-MM-DD'), today.endOf('month').format('YYYY-MM-DD'));
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
    fetchReservations(date.startOf('month').format('YYYY-MM-DD'), date.endOf('month').format('YYYY-MM-DD'));
  };

  // 연 뷰 날짜 클릭 → 해당 날짜 기준 주 뷰로 이동
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

  const handleEventClick = async (reservationId: string) => {
    const result = await businessReservationService.getReservationDetail(businessId, reservationId);
    if (!result.success || !result.data) { toast.error('상세 정보를 불러오는 데 실패했습니다.'); return; }
    setDetailModal({ isOpen: true, detail: result.data });
  };

  const fcEvents: EventInput[] = allReservations
    .filter(r => activeStatuses.has(r.status))
    .map(r => {
      const filter = RESERVATION_STATUS_FILTERS.find(f => f.value === r.status);
      return {
        id: r.reservationId,
        title: `${r.customerName} · ${r.reservationDuration}분`,
        start: `${r.reservationDate}T${r.reservationTime}`,
        end: dayjs(`${r.reservationDate}T${r.reservationTime}`).add(r.reservationDuration, 'minute').format('YYYY-MM-DDTHH:mm:ss'),
        backgroundColor: filter?.eventColor ?? '#e5e7eb',
        borderColor: filter?.eventColor ?? '#e5e7eb',
        textColor: filter?.eventTextColor ?? '#374151',
      };
    });

  return (
    // -m-4: 레이아웃 padding 상쇄
    // min-height: 작은 화면에서도 그리드 최소 크기 보장 → 이하는 스크롤
    <div
      ref={containerRef}
      className="-m-4 flex flex-col"
      style={{
        height: 'calc(100vh - 64px)',
        minHeight: CALENDAR_MIN_HEIGHT,
        // 월/연 뷰에서만 scale 축소 적용 (FullCalendar는 transform 호환 안됨)
        transformOrigin: 'top left',
        transform: scale < 1 && (currentView === 'month' || currentView === 'year')
          ? `scale(${scale})` : undefined,
        width: scale < 1 && (currentView === 'month' || currentView === 'year')
          ? `${100 / scale}%` : undefined,
        minWidth: currentView === 'timeGridWeek' || currentView === 'timeGridDay'
          ? 506 : undefined,
      }}
    >
      {/* 툴바 + 필터 */}
      <div className="px-4 pt-4 pb-2 shrink-0 space-y-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-lg font-medium min-w-[180px]">{titleText}</span>
            <div className="flex border border-border rounded-md overflow-hidden">
              <Button variant="ghost" size="sm" className="rounded-none border-r border-border" onClick={() => handleNavigate(-1)}>‹</Button>
              <Button variant="ghost" size="sm" className="rounded-none" onClick={() => handleNavigate(1)}>›</Button>
            </div>
            <Button variant="outline" size="sm" onClick={handleToday}>오늘</Button>
          </div>
          <div className="flex border border-border rounded-md overflow-hidden">
            {(Object.keys(VIEW_LABELS) as ViewType[]).map(v => (
              <Button
                key={v}
                variant={currentView === v ? 'default' : 'ghost'}
                size="sm"
                className="rounded-none border-r last:border-r-0 border-border"
                onClick={() => handleViewChange(v)}
              >
                {VIEW_LABELS[v]}
              </Button>
            ))}
          </div>
        </div>

        <div className="flex items-center gap-2">
          {RESERVATION_STATUS_FILTERS.map(f => (
            <Button
              key={f.value}
              variant="outline"
              size="sm"
              className={`rounded-full text-xs transition-colors
                ${activeStatuses.has(f.value) ? f.activeClass : 'bg-background text-muted-foreground border-border'}`}
              onClick={() => toggleStatus(f.value)}
            >
              {activeStatuses.has(f.value) ? '● ' : '○ '}{f.label}
            </Button>
          ))}
        </div>
      </div>

      {/* 월 뷰 */}
      {currentView === 'month' && (
        <div className="flex-1 min-h-0 flex flex-col px-4 pb-4">
          <CalendarMonthView
            year={currentDate.year()}
            month={currentDate.month()}
            reservations={allReservations}
            activeStatuses={activeStatuses}
            onEventClick={handleEventClick}
            onMoreClick={handleMoreClick}
          />
        </div>
      )}

      {/* 연 뷰 */}
      {currentView === 'year' && (
        <div className="flex-1 overflow-y-auto px-4 pb-4">
          <CalendarYearView
            year={yearViewYear}
            onMonthClick={handleMonthClick}
            onDayClick={handleDayClick}
          />
        </div>
      )}

      {/* 주/일 뷰 */}
      <div className={`flex-1 min-h-0 px-4 pb-4
        ${currentView === 'timeGridWeek' || currentView === 'timeGridDay' ? '' : 'hidden'}`}>
        <FullCalendar
          ref={calendarRef}
          plugins={[timeGridPlugin, interactionPlugin]}
          initialView="timeGridWeek"
          locale={koLocale}
          height="100%"
          headerToolbar={false}
          events={fcEvents}
          eventClick={(arg: EventClickArg) => handleEventClick(arg.event.id)}
          datesSet={handleDatesSet}
          allDaySlot={false}
          nowIndicator
          stickyHeaderDates={false}
        />
      </div>

      <ReservationDetailModal
        detail={detailModal.detail}
        isOpen={detailModal.isOpen}
        onClose={() => setDetailModal({ isOpen: false, detail: null })}
      />
    </div>
  );
}