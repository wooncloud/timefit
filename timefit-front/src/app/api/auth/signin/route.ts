import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';

import {
  SigninApiResponse,
  SigninHandlerErrorResponse,
  SigninHandlerResponse,
  SigninHandlerSuccessResponse,
  SigninRequestBody,
  SigninSuccessPayload,
} from '@/types/auth/signin';
import {
  SessionData,
  sessionOptions,
  SessionUser,
} from '@/lib/session/options';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as SigninRequestBody;

    const response = await fetch(`${BACKEND_API_URL}/api/auth/signin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    });

    const responseData = (await response.json()) as SigninApiResponse;

    if (!response.ok) {
      const errorPayload: SigninHandlerErrorResponse = {
        success: false,
        message: responseData.message || '로그인에 실패했습니다.',
      };

      const responseJson = NextResponse.json<SigninHandlerResponse>(
        errorPayload,
        { status: response.status }
      );
      const session = await getIronSession<SessionData>(
        request,
        responseJson,
        sessionOptions
      );
      session.destroy();
      return responseJson;
    }

    const headerAccessToken = response.headers
      .get('Authorization')
      ?.replace('Bearer ', '');
    const payloadAccessToken = responseData.data?.accessToken;
    const accessToken = payloadAccessToken || headerAccessToken;

    // refreshToken 추출 (백엔드 응답에서)
    const refreshToken = responseData.data?.refreshToken;

    if (!accessToken) {
      const errorPayload: SigninHandlerErrorResponse = {
        success: false,
        message: '액세스 토큰이 응답에 포함되어 있지 않습니다.',
      };
      const responseJson = NextResponse.json<SigninHandlerResponse>(
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

    const successData = {
      ...(responseData.data ?? {}),
      accessToken,
    } as SigninSuccessPayload;

    const successPayload: SigninHandlerSuccessResponse = {
      success: true,
      message: '로그인에 성공했습니다.',
      data: successData,
    };

    const responseJson =
      NextResponse.json<SigninHandlerResponse>(successPayload);
    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );
    const { accessToken: _, ...userProfile } = successData;
    session.user = {
      ...(userProfile as SessionUser),
      accessToken,
      refreshToken, // refreshToken도 세션에 저장
    };
    await session.save();
    return responseJson;
  } catch (error) {
    console.error('로그인 API 오류:', error);
    const errorPayload = {
      success: false,
      message: '서버 오류가 발생했습니다.',
    } satisfies SigninHandlerErrorResponse;

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
