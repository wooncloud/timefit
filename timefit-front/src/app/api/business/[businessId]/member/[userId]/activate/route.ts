import { NextRequest, NextResponse } from 'next/server';

import type {
  MemberStatusChangeApiResponse,
  MemberStatusChangeHandlerResponse,
} from '@/types/business/team-member';
import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export const PATCH = withAuth<MemberStatusChangeHandlerResponse>(
  async (request: NextRequest, { accessToken }) => {
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

    try {
      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}/member/${userId}/activate`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
      );

      const responseData: MemberStatusChangeApiResponse = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: responseData.message || '활성화에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        message: '구성원이 활성화되었습니다.',
      });
    } catch (error) {
      console.error('Failed to activate member:', error);
      return NextResponse.json(
        {
          success: false,
          message: '활성화 중 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
