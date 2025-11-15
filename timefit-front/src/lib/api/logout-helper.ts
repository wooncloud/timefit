import { NextResponse } from 'next/server';

import { getServerSession } from '@/lib/session/server';

/**
 * 세션을 초기화하고 로그아웃 응답을 반환합니다.
 * API 라우트에서 인증 실패 시 사용됩니다.
 */
export async function clearSessionAndLogout(message = '인증이 만료되었습니다.') {
  const session = await getServerSession();

  // 세션 초기화
  session.destroy();

  return NextResponse.json(
    {
      success: false,
      message,
      requiresLogout: true, // 클라이언트에서 로그아웃 처리를 위한 플래그
    },
    { status: 401 }
  );
}
