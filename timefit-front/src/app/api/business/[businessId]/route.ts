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
 * 업체 상세 정보 조회 (공개 API)
 * 권한: 인증 불필요
 *
 * @route GET /api/business/:businessId
 * @param businessId - 조회할 업체 ID (UUID)
 */
export async function GET(
  request: NextRequest,
  { params }: { params: { businessId: string } }
): Promise<NextResponse> {
  try {
    const { businessId } = params;

    console.log('업체 상세 조회 (공개):', businessId);

    // 백엔드 공개 API 호출 (인증 불필요)
    const response = await fetch(
      `${BACKEND_API_URL}/api/business/${businessId}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(data, { status: response.status });
    }

    return NextResponse.json(data);
  } catch (error) {
    console.error('업체 상세 조회 오류:', error);
    return NextResponse.json(
      {
        errorResponse: {
          code: 'INTERNAL_ERROR',
          message: '업체 정보를 불러오는데 실패했습니다.',
        },
      },
      { status: 500 }
    );
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
