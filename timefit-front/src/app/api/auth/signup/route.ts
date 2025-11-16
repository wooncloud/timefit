import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';
import {
  SignupApiResponse,
  SignupHandlerErrorResponse,
  SignupHandlerResponse,
  SignupHandlerSuccessResponse,
  SignupRequestBody,
  SignupSuccessPayload,
} from '@/types/auth/signup';
import {
  sessionOptions,
  SessionData,
  SessionUser,
} from '@/lib/session/options';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as SignupRequestBody;

    const signupData: SignupRequestBody = {
      email: body.email,
      password: body.password,
      name: body.name,
      phoneNumber: body.phoneNumber,
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
        message: responseData.message || '회원가입에 실패했습니다.',
      };
      const responseJson = NextResponse.json<SignupHandlerResponse>(
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
    const payloadAccessToken = (
      responseData.data as SignupSuccessPayload | undefined
    )?.accessToken;
    const accessToken = payloadAccessToken || headerAccessToken;
    const refreshToken = (
      responseData.data as SignupSuccessPayload | undefined
    )?.refreshToken;

    if (!accessToken) {
      const errorPayload: SignupHandlerErrorResponse = {
        success: false,
        message: '액세스 토큰이 응답에 포함되어 있지 않습니다.',
      };
      const responseJson = NextResponse.json<SignupHandlerResponse>(
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

    if (!refreshToken) {
      const errorPayload: SignupHandlerErrorResponse = {
        success: false,
        message: '리프레시 토큰이 응답에 포함되어 있지 않습니다.',
      };
      const responseJson = NextResponse.json<SignupHandlerResponse>(
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
      refreshToken,
    } as SignupSuccessPayload;

    const successPayload: SignupHandlerSuccessResponse = {
      success: true,
      message: '회원가입에 성공했습니다.',
      data: successData,
    };

    const responseJson =
      NextResponse.json<SignupHandlerResponse>(successPayload);
    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );
    const { accessToken: _, refreshToken: __, ...userProfile } = successData;
    session.user = {
      ...(userProfile as SessionUser),
      accessToken,
      refreshToken,
    };
    await session.save();
    return responseJson;
  } catch (error) {
    console.error('회원가입 API 오류:', error);
    const errorPayload = {
      success: false,
      message: '서버 오류가 발생했습니다.',
    } satisfies SignupHandlerErrorResponse;

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
