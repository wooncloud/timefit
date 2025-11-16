import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';
import {
  RefreshRequestBody,
  RefreshApiResponse,
  RefreshHandlerResponse,
  RefreshHandlerSuccessResponse,
  RefreshHandlerErrorResponse,
} from '@/types/auth/refresh';
import { sessionOptions, SessionData } from '@/lib/session/options';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as RefreshRequestBody;

    if (!body.refreshToken) {
      const errorPayload: RefreshHandlerErrorResponse = {
        success: false,
        message: '리프레시 토큰이 필요합니다.',
      };
      return NextResponse.json<RefreshHandlerResponse>(errorPayload, {
        status: 400,
      });
    }

    const response = await fetch(`${BACKEND_API_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    const responseData = (await response.json()) as RefreshApiResponse;

    if (!response.ok) {
      const errorPayload: RefreshHandlerErrorResponse = {
        success: false,
        message: responseData.message || '토큰 갱신에 실패했습니다.',
      };

      const responseJson = NextResponse.json<RefreshHandlerResponse>(
        errorPayload,
        { status: response.status }
      );

      // 리프레시 토큰도 만료된 경우 세션 파괴
      if (response.status === 401) {
        const session = await getIronSession<SessionData>(
          request,
          responseJson,
          sessionOptions
        );
        session.destroy();
      }

      return responseJson;
    }

    const newAccessToken = responseData.data?.accessToken;
    const newRefreshToken = responseData.data?.refreshToken;

    if (!newAccessToken || !newRefreshToken) {
      const errorPayload: RefreshHandlerErrorResponse = {
        success: false,
        message: '새로운 토큰이 응답에 포함되어 있지 않습니다.',
      };
      const responseJson = NextResponse.json<RefreshHandlerResponse>(
        errorPayload,
        { status: 500 }
      );
      const session = await getIronSession<SessionData>(
        request,
        responseJson,
        sessionOptions
      );
      session.destroy();
      return responseJson;
    }

    const successPayload: RefreshHandlerSuccessResponse = {
      success: true,
      message: '토큰이 갱신되었습니다.',
      data: responseData.data!,
    };

    const responseJson =
      NextResponse.json<RefreshHandlerResponse>(successPayload);

    // 세션 업데이트
    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );

    if (session.user) {
      session.user.accessToken = newAccessToken;
      session.user.refreshToken = newRefreshToken;
      await session.save();
    }

    return responseJson;
  } catch (error) {
    console.error('토큰 갱신 API 오류:', error);
    const errorPayload: RefreshHandlerErrorResponse = {
      success: false,
      message: '서버 오류가 발생했습니다.',
    };

    const responseJson = NextResponse.json(errorPayload, { status: 500 });
    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );
    session.destroy();
    return responseJson;
  }
}
