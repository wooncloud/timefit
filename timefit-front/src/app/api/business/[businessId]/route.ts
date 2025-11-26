import { NextResponse } from 'next/server';

import type {
  GetBusinessDetailApiResponse,
  GetBusinessDetailHandlerResponse,
  UpdateBusinessApiResponse,
  UpdateBusinessHandlerResponse,
  UpdateBusinessRequest,
} from '@/types/business/business-detail';
import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

/**
 * 업체 상세 정보 조회
 * 권한: 인증 필요
 *
 * @route GET /api/business/:businessId
 * @param businessId - 조회할 업체 ID (UUID)
 */
export const GET = withAuth<GetBusinessDetailHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      // URL에서 businessId 추출
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/');
      const businessId = pathSegments[pathSegments.length - 1];

      console.log('업체 상세 조회:', businessId);

      // 백엔드 API 호출
      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}`,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      const result: GetBusinessDetailApiResponse = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: result.message || '업체 정보 조회에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        data: result.data,
      });
    } catch (error) {
      console.error('업체 상세 조회 오류:', error);
      return NextResponse.json(
        {
          success: false,
          message: '서버 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);

/**
 * 업체 정보 수정
 * 권한: OWNER, MANAGER만 가능
 *
 * @route PUT /api/business/:businessId
 * @param businessId - 수정할 업체 ID (UUID)
 */
export const PUT = withAuth<UpdateBusinessHandlerResponse>(
  async (request, { accessToken, userId }) => {
    try {
      // URL에서 businessId 추출
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/');
      const businessId = pathSegments[pathSegments.length - 1];

      // 요청 본문 파싱
      const body: UpdateBusinessRequest = await request.json();

      console.log('업체 정보 수정:', { businessId, userId });

      // 백엔드 API 호출
      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`,
          },
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
      console.error('업체 정보 수정 오류:', error);
      return NextResponse.json(
        {
          success: false,
          message: '서버 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
