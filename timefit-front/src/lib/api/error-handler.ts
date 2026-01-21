import { NextResponse } from 'next/server';

/**
 * API Route에서 발생한 에러를 처리하고 적절한 응답을 반환
 *
 * @param error - 발생한 에러 객체
 * @param defaultMessage - 기본 에러 메시지 (선택사항)
 * @returns NextResponse 객체
 *
 * @example
 * export async function POST(request: NextRequest) {
 *   try {
 *     const response = await apiFetch(url, { method: 'POST', body });
 *     // ... 성공 처리
 *   } catch (error) {
 *     return handleApiError(error);
 *   }
 * }
 */
export function handleApiError<T = unknown>(
  error: unknown,
  defaultMessage = '서버 오류가 발생했습니다.'
): NextResponse<T> {
  const message = error instanceof Error ? error.message : defaultMessage;

  // 인증 관련 에러인 경우 (세션 만료, 토큰 만료 등)
  if (
    message.includes('인증') ||
    message.includes('만료') ||
    message.includes('세션')
  ) {
    return NextResponse.json(
      {
        success: false,
        message,
        requiresLogout: true,
        redirectTo: '/business',
      } as T,
      { status: 401 }
    );
  }

  // 일반 서버 에러
  console.error('API 에러:', error);
  return NextResponse.json(
    {
      success: false,
      message,
    } as T,
    { status: 500 }
  );
}
