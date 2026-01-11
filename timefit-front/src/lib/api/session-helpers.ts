import 'server-only';

import { getServerSession } from '@/lib/session/server';
import type { SessionUser } from '@/lib/session/options';

/**
 * 세션의 토큰만 업데이트
 *
 * @param accessToken - 새로운 액세스 토큰
 * @param refreshToken - 새로운 리프레시 토큰
 */
export async function updateSessionTokens(
  accessToken: string,
  refreshToken: string
): Promise<void> {
  const session = await getServerSession();

  if (!session.user) {
    throw new Error('세션 사용자 정보가 없습니다.');
  }

  session.user = {
    ...(session.user as SessionUser),
    accessToken,
    refreshToken,
  };

  try {
    await session.save();
  } catch (error) {
    // Server Component 렌더링 중에는 쿠키를 수정할 수 없으므로 에러를 무시합니다.
    // 현재 요청은 메모리 상의 새 토큰으로 계속 진행됩니다.
    console.warn(
      '[Session Helpers] 세션 업데이트 건너뜀 (렌더링 컨텍스트):',
      error instanceof Error ? error.message : error
    );
  }
}

/**
 * 세션 전체 삭제 (로그아웃)
 */
export async function clearSession(): Promise<void> {
  const session = await getServerSession();
  try {
    session.destroy();
  } catch (error) {
    console.warn(
      '[Session Helpers] 세션 삭제 건너뜀 (렌더링 컨텍스트):',
      error instanceof Error ? error.message : error
    );
  }
}
