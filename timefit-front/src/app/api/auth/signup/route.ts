import { NextRequest, NextResponse } from 'next/server';
import {
  SignupApiResponse,
  SignupHandlerErrorResponse,
  SignupHandlerResponse,
  SignupHandlerSuccessResponse,
  SignupRequestBody
} from '@/types/auth/signup';
import {
  clearAccessTokenCookie,
  setAccessTokenCookie
} from '@/lib/cookie';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as SignupRequestBody;

    const signupData: SignupRequestBody = {
      email: body.email,
      password: body.password,
      name: body.name,
      phoneNumber: body.phoneNumber
    };

    const response = await fetch(`${BACKEND_API_URL}/api/auth/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(signupData),
    });

    const responseData = (await response.json()) as SignupApiResponse;

    if (!response.ok) {
      const errorPayload: SignupHandlerErrorResponse = {
        success: false,
        message: responseData.message || '회원가입에 실패했습니다.'
      };
      clearAccessTokenCookie();
      return NextResponse.json<SignupHandlerResponse>(errorPayload, { status: response.status });
    }

    const headerAccessToken = response.headers.get('Authorization')?.replace('Bearer ', '');
    const payloadAccessToken = (responseData.data as { accessToken?: string } | undefined)?.accessToken;
    const accessToken = payloadAccessToken || headerAccessToken;

    if (!accessToken) {
      const errorPayload: SignupHandlerErrorResponse = {
        success: false,
        message: '액세스 토큰이 응답에 포함되어 있지 않습니다.'
      };
      clearAccessTokenCookie();
      return NextResponse.json<SignupHandlerResponse>(errorPayload, { status: 500 });
    }

    setAccessTokenCookie(accessToken);

    const successPayload: SignupHandlerSuccessResponse = {
      success: true,
      message: '회원가입에 성공했습니다.',
      data: {
        ...(responseData.data ?? {}),
        accessToken
      }
    };

    return NextResponse.json<SignupHandlerResponse>(successPayload);

  } catch (error) {
    console.error('회원가입 API 오류:', error);
    clearAccessTokenCookie();
    return NextResponse.json(
      {
        success: false,
        message: '서버 오류가 발생했습니다.'
      },
      { status: 500 }
    );
  }
}
