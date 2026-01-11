import { NextRequest, NextResponse } from 'next/server';

import type {
  GetTeamMembersApiResponse,
  GetTeamMembersHandlerResponse,
} from '@/types/business/team-member';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetTeamMembersHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessId = pathSegments[pathSegments.indexOf('business') + 1];

    if (!businessId) {
      return NextResponse.json(
        {
          success: false,
          message: '비즈니스 ID가 필요합니다.',
        },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/members`,
      { method: 'GET' }
    );

    const responseData: GetTeamMembersApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message:
            responseData.message || '구성원 목록을 가져올 수 없습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: responseData.data,
    });
  } catch (error) {
    return handleApiError<GetTeamMembersHandlerResponse>(error);
  }
}
