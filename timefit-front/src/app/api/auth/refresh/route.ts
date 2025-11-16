import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';
import { sessionOptions, SessionData } from '@/lib/session/options';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

interface RefreshTokenRequest {
  refreshToken: string;
}

interface RefreshTokenResponse {
  success: boolean;
  message?: string;
  data?: {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
  };
}

export async function POST(request: NextRequest) {
  try {
    const session = await getIronSession<SessionData>(
      request,
      NextResponse.next(),
      sessionOptions
    );

    // 세션에서 refreshToken 가져오기
    const refreshToken = session.user?.refreshToken;

    if (!refreshToken) {
      return NextResponse.json(
        { success: false, message: 'Refresh token not found' },
        { status: 401 }
      );
    }

    // 백엔드에 토큰 갱신 요청
    const response = await fetch(`${BACKEND_API_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ refreshToken } as RefreshTokenRequest),
    });

    const data = (await response.json()) as RefreshTokenResponse;

    if (!response.ok || !data.success || !data.data) {
      // Refresh token도 만료된 경우
      const responseJson = NextResponse.json(
        { success: false, message: data.message || 'Token refresh failed' },
        { status: response.status }
      );

      const newSession = await getIronSession<SessionData>(
        request,
        responseJson,
        sessionOptions
      );
      newSession.destroy();
      return responseJson;
    }

    // 세션에 새로운 토큰 저장
    const responseJson = NextResponse.json({
      success: true,
      data: {
        accessToken: data.data.accessToken,
        refreshToken: data.data.refreshToken,
      },
    });

    const newSession = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );

    if (newSession.user) {
      newSession.user.accessToken = data.data.accessToken;
      newSession.user.refreshToken = data.data.refreshToken;
      await newSession.save();
    }

    return responseJson;
  } catch (error) {
    console.error('Token refresh error:', error);
    return NextResponse.json(
      { success: false, message: 'Server error during token refresh' },
      { status: 500 }
    );
  }
}
