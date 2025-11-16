import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';
import { sessionOptions, SessionData } from '@/lib/session/options';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const session = await getIronSession<SessionData>(
      request,
      NextResponse.next(),
      sessionOptions
    );

    const accessToken = session.user?.accessToken;

    // 백엔드에 로그아웃 요청 (선택적)
    if (accessToken) {
      try {
        await fetch(`${BACKEND_API_URL}/api/auth/logout`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        });
      } catch (error) {
        console.error('Backend logout error:', error);
        // 백엔드 로그아웃 실패해도 계속 진행
      }
    }

    // 세션 파괴
    const responseJson = NextResponse.json({
      success: true,
      message: 'Logged out successfully',
    });

    const newSession = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );
    newSession.destroy();

    return responseJson;
  } catch (error) {
    console.error('Logout error:', error);
    return NextResponse.json(
      { success: false, message: 'Logout failed' },
      { status: 500 }
    );
  }
}
