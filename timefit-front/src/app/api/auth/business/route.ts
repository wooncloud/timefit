import { NextRequest, NextResponse } from 'next/server';

import {
  CreateBusinessApiResponse,
  CreateBusinessHandlerErrorResponse,
  CreateBusinessHandlerResponse,
  CreateBusinessHandlerSuccessResponse,
  CreateBusinessRequestBody,
  CreateBusinessSuccessPayload,
} from '@/types/auth/business/createBusiness';
import { getServerSession } from '@/lib/session/server';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as CreateBusinessRequestBody;

    const session = await getServerSession();
    const accessToken = session.user?.accessToken;

    // // 디버깅 로그
    // console.log('[Business API] Session 확인:', {
    //   hasSession: !!session,
    //   hasUser: !!session.user,
    //   hasAccessToken: !!accessToken,
    //   userEmail: session.user?.email,
    //   userId: session.user?.userId,
    // });

    if (!accessToken) {
      console.error('[Business API] AccessToken 없음 - 로그인 필요');
      const errorPayload: CreateBusinessHandlerErrorResponse = {
        success: false,
        message: '로그인이 필요합니다.',
      };
      return NextResponse.json<CreateBusinessHandlerResponse>(errorPayload, {
        status: 401,
      });
    }

    // console.log('[Business API] Backend 요청:', {
    //   url: `${BACKEND_API_URL}/api/business`,
    //   method: 'POST',
    //   hasAuthHeader: true,
    //   body: body,
    // });

    const response = await fetch(`${BACKEND_API_URL}/api/business`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(body),
    });

    // console.log('[Business API] Backend 응답:', {
    //   status: response.status,
    //   statusText: response.statusText,
    //   ok: response.ok,
    // });

    let responseData: CreateBusinessApiResponse | null = null;
    try {
      responseData = (await response.json()) as CreateBusinessApiResponse;
      // console.log('[Business API] 응답 데이터:', responseData);
    } catch (parseError) {
      console.warn('[Business API] 응답 파싱 실패:', parseError);
    }

    if (!response.ok) {
      console.error('[Business API] 요청 실패:', {
        status: response.status,
        message: responseData?.message,
      });
      const errorPayload: CreateBusinessHandlerErrorResponse = {
        success: false,
        message: responseData?.message || '사업자 등록에 실패했습니다.',
      };
      return NextResponse.json<CreateBusinessHandlerResponse>(errorPayload, {
        status: response.status,
      });
    }

    const successPayload: CreateBusinessHandlerSuccessResponse = {
      success: true,
      message: '사업자 등록에 성공했습니다.',
      data: (responseData?.data ?? {}) as CreateBusinessSuccessPayload,
    };

    const statusCode = response.status === 204 ? 200 : response.status;

    return NextResponse.json<CreateBusinessHandlerResponse>(successPayload, {
      status: statusCode,
    });
  } catch (error) {
    console.error('사업자 등록 API 오류:', error);
    return NextResponse.json<CreateBusinessHandlerResponse>(
      {
        success: false,
        message: '서버 오류가 발생했습니다.',
      },
      { status: 500 }
    );
  }
}
