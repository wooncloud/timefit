import { NextRequest, NextResponse } from 'next/server';
import { withAuth } from '@/lib/api/auth-middleware';
import type {
  ChangeMemberRoleRequest,
  ChangeMemberRoleApiResponse,
  ChangeMemberRoleHandlerResponse,
} from '@/types/business/teamMember';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export const PATCH = withAuth<ChangeMemberRoleHandlerResponse>(
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
      const body: ChangeMemberRoleRequest = await request.json();

      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}/member/${userId}/role`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(body),
        }
      );

      const responseData: ChangeMemberRoleApiResponse = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: responseData.message || '권한 변경에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        message: '권한이 변경되었습니다.',
      });
    } catch (error) {
      console.error('Failed to change member role:', error);
      return NextResponse.json(
        {
          success: false,
          message: '권한 변경 중 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
