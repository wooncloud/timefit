import { cookies } from 'next/headers';
import { NextRequest, NextResponse } from 'next/server';

import {
  SigninApiResponse,
  SigninHandlerErrorResponse,
  SigninHandlerResponse,
  SigninHandlerSuccessResponse,
  SigninRequestBody,
  SigninSuccessPayload
} from '@/types/auth/signin';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';
const ACCESS_TOKEN_COOKIE_NAME = 'accessToken';
const ACCESS_TOKEN_MAX_AGE = 60 * 15; // 15 minutes

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
        message: responseData.message || '로그인에 실패했습니다.'
      };

      // 실패 시 토큰 쿠키 제거 시도
      (await cookies()).delete(ACCESS_TOKEN_COOKIE_NAME);
      return NextResponse.json<SigninHandlerResponse>(errorPayload, { status: response.status });
    }

    const headerAccessToken = response.headers.get('Authorization')?.replace('Bearer ', '');
    const payloadAccessToken = responseData.data?.accessToken;
    const accessToken = payloadAccessToken || headerAccessToken;

    if (!accessToken) {
      const errorPayload: SigninHandlerErrorResponse = {
        success: false,
        message: '액세스 토큰이 응답에 포함되어 있지 않습니다.'
      };
      return NextResponse.json<SigninHandlerResponse>(errorPayload, { status: 500 });
    }

    (await cookies()).set({
      name: ACCESS_TOKEN_COOKIE_NAME,
      value: accessToken,
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      path: '/',
      maxAge: ACCESS_TOKEN_MAX_AGE,
    });

    const successData: SigninSuccessPayload = {
      ...(responseData.data ?? {}),
      accessToken,
    };

    const successPayload: SigninHandlerSuccessResponse = {
      success: true,
      message: '로그인에 성공했습니다.',
      data: successData,
    };

    return NextResponse.json<SigninHandlerResponse>(successPayload);

  } catch (error) {
    console.error('로그인 API 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '서버 오류가 발생했습니다.'
      },
      { status: 500 }
    );
  }
}
