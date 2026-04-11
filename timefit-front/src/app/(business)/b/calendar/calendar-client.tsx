'use client';

import FullCalendar from '@fullcalendar/react';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import koLocale from '@fullcalendar/core/locales/ko';
import type { EventClickArg } from '@fullcalendar/core';

import type { BusinessReservationItem } from '@/types/business/reservation';
import type { ViewType } from '@/types/business/calendar';
import { useCalendar } from '@/hooks/business/calendar/use-calendar';
import { ReservationDetailModal } from '@/components/business/reservations/reservation-detail-modal';
import { CalendarMonthView } from '@/components/business/calendar/calendar-month-view';
import { CalendarYearView } from '@/components/business/calendar/calendar-year-view';
import { Button } from '@/components/ui/button';
import { VIEW_LABELS } from '@/lib/constants/calendar';
import { RESERVATION_STATUS_FILTERS } from '@/lib/constants/reservation-status';

import '@/styles/fullcalendar-override.css';

interface CalendarClientProps {
  initialReservations: BusinessReservationItem[];
  businessId: string;
}

export function CalendarClient({ initialReservations, businessId }: CalendarClientProps) {
  const { view, scale, filter, modal, data, fcEvents } = useCalendar({ initialReservations, businessId });

  return (
    <div ref={scale.containerRef} className="-m-4 flex flex-col" style={scale.containerStyle}>
      {/* 툴바 + 필터 */}
      <div className="px-4 pt-4 pb-2 shrink-0 space-y-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-lg font-medium min-w-[180px]">{view.titleText}</span>
            <div className="flex border border-border rounded-md overflow-hidden">
              <Button variant="ghost" size="sm" className="rounded-none border-r border-border" onClick={() => view.handleNavigate(-1)}>‹</Button>
              <Button variant="ghost" size="sm" className="rounded-none" onClick={() => view.handleNavigate(1)}>›</Button>
            </div>
            <Button variant="outline" size="sm" onClick={view.handleToday}>오늘</Button>
          </div>
          <div className="flex border border-border rounded-md overflow-hidden">
            {(Object.keys(VIEW_LABELS) as ViewType[]).map(v => (
              <Button
                key={v}
                variant={view.currentView === v ? 'default' : 'ghost'}
                size="sm"
                className="rounded-none border-r last:border-r-0 border-border"
                onClick={() => view.handleViewChange(v)}
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
                ${filter.activeStatuses.has(f.value) ? f.activeClass : 'bg-background text-muted-foreground border-border'}`}
              onClick={() => filter.toggleStatus(f.value)}
            >
              {filter.activeStatuses.has(f.value) ? '● ' : '○ '}{f.label}
            </Button>
          ))}
        </div>
      </div>

      {/* 월 뷰 */}
      {view.currentView === 'month' && (
        <div className="flex-1 min-h-0 flex flex-col px-4 pb-4">
          <CalendarMonthView
            year={view.currentDate.year()}
            month={view.currentDate.month()}
            reservations={data.allReservations}
            activeStatuses={filter.activeStatuses}
            onEventClick={modal.handleEventClick}
            onMoreClick={view.handleMoreClick}
          />
        </div>
      )}

      {/* 연 뷰 */}
      {view.currentView === 'year' && (
        <div className="flex-1 overflow-y-auto px-4 pb-4">
          <CalendarYearView
            year={view.yearViewYear}
            onMonthClick={view.handleMonthClick}
            onDayClick={view.handleDayClick}
          />
        </div>
      )}

      {/* 주/일 뷰 */}
      <div className={`flex-1 min-h-0 px-4 pb-4
        ${view.currentView === 'timeGridWeek' || view.currentView === 'timeGridDay' ? '' : 'hidden'}`}>
        <FullCalendar
          ref={view.calendarRef}
          plugins={[timeGridPlugin, interactionPlugin]}
          initialView="timeGridWeek"
          locale={koLocale}
          height="100%"
          headerToolbar={false}
          events={fcEvents}
          eventClick={(arg: EventClickArg) => modal.handleEventClick(arg.event.id)}
          datesSet={view.handleDatesSet}
          allDaySlot={false}
          nowIndicator
          stickyHeaderDates={false}
        />
      </div>

      <ReservationDetailModal
        detail={modal.detailModal.detail}
        isOpen={modal.detailModal.isOpen}
        onClose={modal.closeDetailModal}
      />
    </div>
  );
}