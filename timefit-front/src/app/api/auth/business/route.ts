import { NextRequest, NextResponse } from 'next/server';

import {
  CreateBusinessApiResponse,
  CreateBusinessHandlerErrorResponse,
  CreateBusinessHandlerResponse,
  CreateBusinessHandlerSuccessResponse,
  CreateBusinessRequestBody,
  CreateBusinessSuccessPayload
} from '@/types/auth/business/createBusiness';
import { getAccessTokenFromCookie } from '@/lib/cookie';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function POST(request: NextRequest) {
  try {
    const body = (await request.json()) as CreateBusinessRequestBody;

    const accessToken = await getAccessTokenFromCookie();
    if (!accessToken) {
      const errorPayload: CreateBusinessHandlerErrorResponse = {
        success: false,
        message: '로그인이 필요합니다.'
      };
      return NextResponse.json<CreateBusinessHandlerResponse>(errorPayload, { status: 401 });
    }

    const response = await fetch(`${BACKEND_API_URL}/api/business`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify(body),
    });

    let responseData: CreateBusinessApiResponse | null = null;
    try {
      responseData = (await response.json()) as CreateBusinessApiResponse;
    } catch (parseError) {
      console.warn('사업자 등록 응답 파싱 실패:', parseError);
    }

    if (!response.ok) {
      const errorPayload: CreateBusinessHandlerErrorResponse = {
        success: false,
        message: responseData?.message || '사업자 등록에 실패했습니다.'
      };
      return NextResponse.json<CreateBusinessHandlerResponse>(errorPayload, { status: response.status });
    }

    const successPayload: CreateBusinessHandlerSuccessResponse = {
      success: true,
      message: '사업자 등록에 성공했습니다.',
      data: (responseData?.data ?? {}) as CreateBusinessSuccessPayload,
    };

    const statusCode = response.status === 204 ? 200 : response.status;

    return NextResponse.json<CreateBusinessHandlerResponse>(successPayload, { status: statusCode });

  } catch (error) {
    console.error('사업자 등록 API 오류:', error);
    return NextResponse.json<CreateBusinessHandlerResponse>(
      {
        success: false,
        message: '서버 오류가 발생했습니다.'
      },
      { status: 500 }
    );
  }
}
