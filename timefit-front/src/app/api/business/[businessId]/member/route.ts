import { NextRequest, NextResponse } from 'next/server';
import { withAuth } from '@/lib/api/auth-middleware';
import type {
  InviteMemberRequest,
  InviteMemberApiResponse,
  InviteMemberHandlerResponse,
} from '@/types/business/teamMember';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export const POST = withAuth<InviteMemberHandlerResponse>(
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

      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}/member`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
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
      console.error('Failed to invite member:', error);
      return NextResponse.json(
        {
          success: false,
          message: '구성원 초대 중 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
