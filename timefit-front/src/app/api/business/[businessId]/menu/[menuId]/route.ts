import { NextResponse } from 'next/server';

import { withAuth } from '@/lib/api/auth-middleware';
import type {
  DeleteProductHandlerResponse,
  GetProductDetailHandlerResponse,
  ProductResponse,
  UpdateProductRequest,
} from '@/types/product/product';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

/**
 * 서비스(메뉴) 상세 조회
 * 권한: Public (인증 불필요)
 *
 * @route GET /api/business/:businessId/menu/:menuId
 * @param businessId - 업체 ID (UUID)
 * @param menuId - 서비스 ID (UUID)
 */
export const GET = withAuth<GetProductDetailHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      // URL에서 businessId, menuId 추출
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/');
      const businessIdIndex = pathSegments.indexOf('business') + 1;
      const menuIdIndex = pathSegments.indexOf('menu') + 1;
      const businessId = pathSegments[businessIdIndex];
      const menuId = pathSegments[menuIdIndex];

      console.log('서비스 상세 조회:', { businessId, menuId });

      // 백엔드 API 호출
      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}/menu/${menuId}`,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      const result: ProductResponse = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: result.message || '서비스 조회에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        data: result.data,
      });
    } catch (error) {
      console.error('서비스 상세 조회 오류:', error);
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
 * 서비스(메뉴) 삭제
 * 권한: OWNER, MANAGER만 가능
 *
 * @route DELETE /api/business/:businessId/menu/:menuId
 * @param businessId - 업체 ID (UUID)
 * @param menuId - 서비스 ID (UUID)
 */
export const DELETE = withAuth<DeleteProductHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      // URL에서 businessId, menuId 추출
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/');
      const businessIdIndex = pathSegments.indexOf('business') + 1;
      const menuIdIndex = pathSegments.indexOf('menu') + 1;
      const businessId = pathSegments[businessIdIndex];
      const menuId = pathSegments[menuIdIndex];

      console.log('서비스 삭제:', { businessId, menuId });

      // 백엔드 API 호출
      const response = await fetch(
        `${BACKEND_API_URL}/api/business/${businessId}/menu/${menuId}`,
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      // DELETE 요청은 204 No Content를 반환할 수 있음
      if (response.status === 204) {
        return NextResponse.json({
          success: true,
          message: '서비스가 삭제되었습니다.',
        });
      }

      const result: { message?: string } = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: result.message || '서비스 삭제에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        message: '서비스가 삭제되었습니다.',
      });
    } catch (error) {
      console.error('서비스 삭제 오류:', error);
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
 * 서비스(메뉴) 수정
 * 권한: OWNER, MANAGER만 가능
 *
 * @route PATCH /api/business/:businessId/menu/:menuId
 * @param businessId - 업체 ID (UUID)
 * @param menuId - 서비스 ID (UUID)
 * @body UpdateProductRequest
 */
export const PATCH = withAuth<{
  success: boolean;
  data?: any;
  message?: string;
}>(async (request, { accessToken }) => {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessIdIndex = pathSegments.indexOf('business') + 1;
    const menuIdIndex = pathSegments.indexOf('menu') + 1;
    const businessId = pathSegments[businessIdIndex];
    const menuId = pathSegments[menuIdIndex];

    const body: UpdateProductRequest = await request.json();

    console.log('서비스 수정:', { businessId, menuId, body });

    const response = await fetch(
      `${BACKEND_API_URL}/api/business/${businessId}/menu/${menuId}`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(body),
      }
    );

    const result: ProductResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: result.message || '서비스 수정에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data,
      message: '서비스가 수정되었습니다.',
    });
  } catch (error) {
    console.error('서비스 수정 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '서버 오류가 발생했습니다.',
      },
      { status: 500 }
    );
  }
});
