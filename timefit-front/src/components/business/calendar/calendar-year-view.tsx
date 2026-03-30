'use client';

import dayjs from 'dayjs';

interface CalendarYearViewProps {
  year: number;
  onMonthClick: (year: number, month: number) => void;
  onDayClick: (year: number, month: number, day: number) => void;
}

const DAYS = ['일', '월', '화', '수', '목', '금', '토'];

function MiniMonth({
                     year,
                     month,
                     today,
                     onMonthClick,
                     onDayClick,
                   }: {
  year: number;
  month: number;
  today: dayjs.Dayjs;
  onMonthClick: (year: number, month: number) => void;
  onDayClick: (year: number, month: number, day: number) => void;
}) {
  const firstDay = dayjs(new Date(year, month, 1));
  const startDow = firstDay.day();
  const daysInMonth = firstDay.daysInMonth();

  // 5주 × 7일 = 35칸
  const cells: (number | null)[] = [];
  for (let i = 0; i < startDow; i++) cells.push(null);
  for (let d = 1; d <= daysInMonth; d++) cells.push(d);
  while (cells.length < 35) cells.push(null);

  return (
    <div className="border border-border rounded-lg overflow-hidden">
      {/* 월 제목 — 클릭 시 월 뷰로 */}
      <button
        className="w-full text-left px-3 py-2 text-sm font-medium border-b border-border hover:bg-muted/50 transition-colors"
        onClick={() => onMonthClick(year, month)}
      >
        {month + 1}월
      </button>

      {/* 요일 헤더 */}
      <div className="grid grid-cols-7 px-1">
        {DAYS.map((d, i) => (
          <div
            key={d}
            className={`text-center text-[10px] py-1 ${
              i === 0 ? 'text-destructive' : i === 6 ? 'text-blue-500' : 'text-muted-foreground'
            }`}
          >
            {d}
          </div>
        ))}
      </div>

      {/* 날짜 그리드 */}
      <div className="grid grid-cols-7 px-1 pb-1">
        {cells.map((day, idx) => {
          if (!day) return <div key={idx} />;
          const isToday =
            today.year() === year &&
            today.month() === month &&
            today.date() === day;
          const dow = (startDow + day - 1) % 7;

          return (
            <button
              key={idx}
              onClick={() => onDayClick(year, month, day)}
              className="flex items-center justify-center py-0.5 rounded hover:bg-muted/60 transition-colors"
            >
              <span
                className={`text-[11px] w-5 h-5 flex items-center justify-center rounded-full
                  ${isToday ? 'bg-primary text-primary-foreground font-medium' : ''}
                  ${!isToday && dow === 0 ? 'text-destructive' : ''}
                  ${!isToday && dow === 6 ? 'text-blue-500' : ''}
                  ${!isToday && dow !== 0 && dow !== 6 ? 'text-foreground' : ''}
                `}
              >
                {day}
              </span>
            </button>
          );
        })}
      </div>
    </div>
  );
}

export function CalendarYearView({ year, onMonthClick, onDayClick }: CalendarYearViewProps) {
  const today = dayjs();

  return (
    <div className="grid grid-cols-4 gap-4 p-4">
      {Array.from({ length: 12 }, (_, i) => (
        <MiniMonth
          key={i}
          year={year}
          month={i}
          today={today}
          onMonthClick={onMonthClick}
          onDayClick={onDayClick}
        />
      ))}
    </div>
  );
}