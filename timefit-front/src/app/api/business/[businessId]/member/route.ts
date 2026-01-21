import { NextRequest, NextResponse } from 'next/server';

import type {
  InviteMemberApiResponse,
  InviteMemberHandlerResponse,
  InviteMemberRequest,
} from '@/types/business/team-member';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function POST(
  request: NextRequest
): Promise<NextResponse<InviteMemberHandlerResponse>> {
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

    const body: InviteMemberRequest = await request.json();

    if (!body.email) {
      return NextResponse.json(
        {
          success: false,
          message: '이메일은 필수입니다.',
        },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/member`,
      {
        method: 'POST',
        body: JSON.stringify(body),
      }
    );

    const responseData: InviteMemberApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '구성원 초대에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: responseData.data,
      message: responseData.message || '구성원을 성공적으로 초대했습니다.',
    });
  } catch (error) {
    return handleApiError<InviteMemberHandlerResponse>(error);
  }
}
