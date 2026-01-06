import { NextRequest, NextResponse } from 'next/server';

import type {
  GetTeamMembersApiResponse,
  GetTeamMembersHandlerResponse,
} from '@/types/business/team-member';
import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export const GET = withAuth<GetTeamMembersHandlerResponse>(
  async (request: NextRequest, { accessToken }) => {
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

    try {
      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}/members`,
        {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
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
      console.error('팀 구성원 조회 실패:', error);
      return NextResponse.json(
        {
          success: false,
          message: '구성원 목록을 가져오는 중 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
