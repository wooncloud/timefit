import { NextRequest, NextResponse } from 'next/server';

import type {
  MemberStatusChangeApiResponse,
  MemberStatusChangeHandlerResponse,
} from '@/types/business/team-member';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function PATCH(
  request: NextRequest
): Promise<NextResponse<MemberStatusChangeHandlerResponse>> {
  try {
    // Extract businessId and userId from URL
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessId = pathSegments[pathSegments.indexOf('business') + 1];
    const userId = pathSegments[pathSegments.indexOf('member') + 1];

    if (!businessId || !userId) {
      return NextResponse.json(
        {
          success: false,
          message: '비즈니스 ID와 사용자 ID가 필요합니다.',
        },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/member/${userId}/deactivate`,
      { method: 'PATCH' }
    );

    const responseData: MemberStatusChangeApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '비활성화에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      message: '구성원이 비활성화되었습니다.',
    });
  } catch (error) {
    return handleApiError<MemberStatusChangeHandlerResponse>(error);
  }
}
