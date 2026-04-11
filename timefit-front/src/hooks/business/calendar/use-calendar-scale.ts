import { useEffect, useMemo, useRef, useState } from 'react';

import type { ViewType } from '@/types/business/calendar';
import { CALENDAR_MIN_HEIGHT } from '@/lib/constants/calendar';

const SCALE_BREAKPOINT = 510;

/**
 * 캘린더 반응형 scale 처리
 * 510px 이하에서 월/연 뷰를 비율 축소
 * FullCalendar(주/일 뷰)는 transform 호환 안되므로 minWidth + 스크롤 처리
 */
export function useCalendarScale(currentView: ViewType) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [scale, setScale] = useState(1);

  useEffect(() => {
    const handleResize = () => {
      const width = window.innerWidth - 256; // 사이드바 너비 제외
      setScale(width < SCALE_BREAKPOINT ? Math.max(0.6, width / SCALE_BREAKPOINT) : 1);
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const containerStyle = useMemo(() => ({
    height: 'calc(100vh - 64px)',
    minHeight: CALENDAR_MIN_HEIGHT,
    transformOrigin: 'top left',
    transform: scale < 1 && (currentView === 'month' || currentView === 'year')
      ? `scale(${scale})` : undefined,
    width: scale < 1 && (currentView === 'month' || currentView === 'year')
      ? `${100 / scale}%` : undefined,
    minWidth: currentView === 'timeGridWeek' || currentView === 'timeGridDay'
      ? 506 : undefined,
  }), [scale, currentView]);

  return { containerRef, containerStyle };
}