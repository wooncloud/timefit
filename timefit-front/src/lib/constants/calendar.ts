import type { ViewType } from '@/types/business/calendar';

export const VIEW_LABELS: Record<ViewType, string> = {
  month: '월',
  timeGridWeek: '주',
  timeGridDay: '일',
  year: '연',
};

// 월 뷰: 요일헤더(34px) + 6행 × 100px = 634px + 툴바/필터 ~90px = 최소 724px
export const CALENDAR_MIN_HEIGHT = 760;