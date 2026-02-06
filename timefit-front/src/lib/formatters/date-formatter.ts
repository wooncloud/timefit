/**
 * 날짜를 한국어 형식으로 포맷팅
 * @param dateString ISO 8601 형식의 날짜 문자열
 * @returns 한국어로 포맷팅된 날짜 문자열 (예: "2026년 2월 6일 16:30")
 */
export function formatDateTime(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

/**
 * 날짜만 한국어 형식으로 포맷팅
 * @param dateString ISO 8601 형식의 날짜 문자열
 * @returns 한국어로 포맷팅된 날짜 문자열 (예: "2026년 2월 6일")
 */
export function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

/**
 * 시간만 한국어 형식으로 포맷팅
 * @param dateString ISO 8601 형식의 날짜 문자열
 * @returns 한국어로 포맷팅된 시간 문자열 (예: "16:30")
 */
export function formatTime(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
  });
}
