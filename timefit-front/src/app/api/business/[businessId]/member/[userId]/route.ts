import { NextRequest, NextResponse } from 'next/server';

import type {
  DeleteMemberApiResponse,
  DeleteMemberHandlerResponse,
} from '@/types/business/team-member';
import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export const DELETE = withAuth<DeleteMemberHandlerResponse>(
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
        `${BACKEND_API_URL}/api/business/${businessId}/member/${userId}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
      );

      if (response.status === 204) {
        return NextResponse.json({
          success: true,
          message: '구성원이 삭제되었습니다.',
        });
      }

      const responseData: DeleteMemberApiResponse = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: responseData.message || '구성원 삭제에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        message: '구성원이 삭제되었습니다.',
      });
    } catch (error) {
      console.error('구성원 삭제 실패:', error);
      return NextResponse.json(
        {
          success: false,
          message: '구성원 삭제 중 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
