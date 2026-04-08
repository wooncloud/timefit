'use client';

import { useEffect, useRef, useState } from 'react';
import dayjs from 'dayjs';

import type { BusinessReservationItem, ReservationStatus } from '@/types/business/reservation';
import { RESERVATION_STATUS_COLOR } from '@/lib/constants/reservation-status';

const DAYS = ['일', '월', '화', '수', '목', '금', '토'];

interface CalendarMonthViewProps {
  year: number;
  month: number;
  reservations: BusinessReservationItem[];
  activeStatuses: Set<string>;
  onEventClick: (reservationId: string) => void;
  onMoreClick: (date: string) => void;
}


export function CalendarMonthView({
                                    year,
                                    month,
                                    reservations,
                                    activeStatuses,
                                    onEventClick,
                                    onMoreClick,
                                  }: CalendarMonthViewProps) {
  const today = dayjs();
  const firstDay = dayjs(new Date(year, month, 1));
  const startDow = firstDay.day();

  const cells: dayjs.Dayjs[] = Array.from({ length: 42 }, (_, i) =>
    firstDay.subtract(startDow - i, 'day')
  );

  const eventsMap: Record<string, BusinessReservationItem[]> = {};
  reservations.forEach(r => {
    if (!activeStatuses.has(r.status)) return;
    if (!eventsMap[r.reservationDate]) eventsMap[r.reservationDate] = [];
    eventsMap[r.reservationDate].push(r);
  });

  // 그리드 컨테이너 높이 측정 → 6행이니 1행 높이 = 전체 / 6
  const gridRef = useRef<HTMLDivElement>(null);
  const [maxVisible, setMaxVisible] = useState(2);

  useEffect(() => {
    const el = gridRef.current;
    if (!el) return;

    const calc = (totalHeight: number) => {
      const cellHeight = totalHeight / 6;
      // 날짜 숫자: 28px, 이벤트 1줄: 22px, 더보기: 18px, 여유: 4px
      const DATE_ROW = 28;
      const EVENT_ROW = 22;
      const MORE_ROW = 22;
      const available = cellHeight - DATE_ROW - MORE_ROW - 4;
      const count = Math.max(1, Math.floor(available / EVENT_ROW));
      setMaxVisible(count);
    };

    // 초기 측정 (렌더 후)
    const initialH = el.getBoundingClientRect().height;
    if (initialH > 0) calc(initialH);

    const observer = new ResizeObserver(entries => {
      const h = entries[0]?.contentRect.height;
      if (h > 0) calc(h);
    });

    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return (
    <div className="flex flex-col h-full min-h-0">
      {/* 요일 헤더 */}
      <div className="grid grid-cols-7 border-b border-t border-border shrink-0">
        {DAYS.map((d, i) => (
          <div
            key={d}
            className={`text-center text-xs py-2 font-medium
              ${i === 0 ? 'text-destructive' : i === 6 ? 'text-blue-500' : 'text-muted-foreground'}`}
          >
            {d}
          </div>
        ))}
      </div>

      {/* 날짜 그리드 */}
      <div
        ref={gridRef}
        className="grid grid-cols-7 h-full"
        style={{ gridTemplateRows: 'repeat(6, 1fr)' }}
      >
        {cells.map((cell, idx) => {
          const isCurrentMonth = cell.month() === month;
          const isToday = cell.isSame(today, 'day');
          const dow = cell.day();
          const dateKey = cell.format('YYYY-MM-DD');
          const dayEvents = eventsMap[dateKey] ?? [];
          const visibleEvents = dayEvents.slice(0, maxVisible);
          const hiddenCount = dayEvents.length - maxVisible;

          return (
            <div
              key={idx}
              className={`border-r border-b border-border p-1 h-full
                ${!isCurrentMonth ? 'bg-muted/20' : ''}
                ${idx % 7 === 6 ? 'border-r-0' : ''}
              `}

            >
              {/* 날짜 숫자 */}
              <div className="mb-0.5">
                <span className={`inline-flex items-center justify-center w-6 h-6 rounded-full text-xs
                  ${isToday ? 'bg-primary text-primary-foreground font-medium' : ''}
                  ${!isToday && !isCurrentMonth ? 'text-muted-foreground/40' : ''}
                  ${!isToday && isCurrentMonth && dow === 0 ? 'text-destructive' : ''}
                  ${!isToday && isCurrentMonth && dow === 6 ? 'text-blue-500' : ''}
                  ${!isToday && isCurrentMonth && dow !== 0 && dow !== 6 ? 'text-foreground' : ''}
                `}>
                  {cell.date()}
                </span>
              </div>

              {/* 이벤트 */}
              {visibleEvents.map(r => {
                const color = RESERVATION_STATUS_COLOR[r.status as ReservationStatus]
                  ?? { bg: 'bg-gray-100', text: 'text-gray-500' };
                return (
                  <button
                    key={r.reservationId}
                    onClick={() => onEventClick(r.reservationId)}
                    className={`w-full text-left text-[11px] px-1.5 py-0.5 rounded mb-0.5 truncate
                      ${color.bg} ${color.text} hover:opacity-75 transition-opacity cursor-pointer`}
                  >
                    {r.reservationTime.slice(0, 5)} {r.customerName}
                  </button>
                );
              })}

              {/* +N건 더보기 */}
              {hiddenCount > 0 && (
                <button
                  onClick={() => onMoreClick(dateKey)}
                  className="w-full text-left text-[10px] text-muted-foreground px-1 hover:text-foreground hover:underline transition-colors cursor-pointer"
                >
                  +{hiddenCount}건 더보기
                </button>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}