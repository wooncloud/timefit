import { NextRequest, NextResponse } from 'next/server';

import type {
  GetBusinessDetailApiResponse,
  GetBusinessDetailHandlerResponse,
  UpdateBusinessApiResponse,
  UpdateBusinessHandlerResponse,
  UpdateBusinessRequest,
} from '@/types/business/business-detail';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

/**
 * 업체 상세 정보 조회
 * 권한: 인증 필요
 *
 * @route GET /api/business/:businessId
 * @param businessId - 조회할 업체 ID (UUID)
 */
export async function GET(
  request: NextRequest
): Promise<NextResponse<GetBusinessDetailHandlerResponse>> {
  try {
    // URL에서 businessId 추출
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessId = pathSegments[pathSegments.length - 1];

    console.log('업체 상세 조회:', businessId);

    // apiFetch가 자동으로 토큰 추가 + 401 처리
    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}`,
      { method: 'GET' }
    );

    const result: GetBusinessDetailApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: result.message || '업체 정보 조회에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data,
    });
  } catch (error) {
    return handleApiError<GetBusinessDetailHandlerResponse>(error);
  }
}

/**
 * 업체 정보 수정
 * 권한: OWNER, MANAGER만 가능
 *
 * @route PUT /api/business/:businessId
 * @param businessId - 수정할 업체 ID (UUID)
 */
export async function PUT(
  request: NextRequest
): Promise<NextResponse<UpdateBusinessHandlerResponse>> {
  try {
    // URL에서 businessId 추출
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessId = pathSegments[pathSegments.length - 1];

    // 요청 본문 파싱
    const body: UpdateBusinessRequest = await request.json();

    console.log('업체 정보 수정:', { businessId });

    // apiFetch가 자동으로 토큰 추가 + 401 처리
    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}`,
      {
        method: 'PUT',
        body: JSON.stringify(body),
      }
    );

    const result: UpdateBusinessApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: result.message || '업체 정보 수정에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data,
      message: '업체 정보가 수정되었습니다.',
    });
  } catch (error) {
    return handleApiError<UpdateBusinessHandlerResponse>(error);
  }
}
