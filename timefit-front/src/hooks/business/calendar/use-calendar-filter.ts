import { useState } from 'react';

/**
 * 캘린더 상태 필터 토글 담당
 * 최소 1개 상태는 항상 활성화 보장
 */
export function useCalendarFilter(initialStatuses: string[] = ['CONFIRMED', 'COMPLETED']) {
  const [activeStatuses, setActiveStatuses] = useState<Set<string>>(new Set(initialStatuses));

  const toggleStatus = (status: string) => {
    setActiveStatuses(prev => {
      const next = new Set(prev);
      if (next.has(status)) {
        if (next.size === 1) return prev; // 최소 1개 유지
        next.delete(status);
      } else {
        next.add(status);
      }
      return next;
    });
  };

  return { activeStatuses, toggleStatus };
}