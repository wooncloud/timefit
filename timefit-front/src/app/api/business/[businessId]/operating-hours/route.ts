import { NextRequest, NextResponse } from 'next/server';

import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function PUT(request: NextRequest): Promise<NextResponse> {
  try {
    // URL에서 businessId 추출 (예: /api/business/[businessId]/operating-hours)
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    // operating-hours 바로 앞의 세그먼트가 businessId
    const businessId = pathSegments[pathSegments.length - 2];

    const body = await request.json();

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/operating-hours`,
      {
        method: 'PUT',
        body: JSON.stringify(body),
      }
    );

    if (!response.ok) {
      let errorMessage = '영업시간 수정에 실패했습니다.';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch (_e) {
        // 응답이 JSON이 아닐 경우 기본 메시지 사용
      }

      return NextResponse.json(
        {
          success: false,
          message: errorMessage,
        },
        { status: response.status }
      );
    }

    const result = await response.json();

    return NextResponse.json({
      success: true,
      data: result.data,
      message: '영업시간이 수정되었습니다.',
    });
  } catch (error) {
    return handleApiError(error);
  }
}
