import { NextResponse } from 'next/server';

import { withAuth } from '@/lib/api/auth-middleware';
import type {
  GetProductListHandlerResponse,
  ProductListResponse,
  CreateProductRequest,
  ProductResponse,
} from '@/types/product/product';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

/**
 * 서비스(메뉴) 목록 조회
 * 권한: Public (인증 불필요)
 *
 * @route GET /api/business/:businessId/menu
 * @param businessId - 업체 ID (UUID)
 * @query serviceName - 서비스명 검색 (부분 일치, 선택)
 * @query businessCategoryId - 카테고리 ID 필터 (선택)
 * @query minPrice - 최소 가격 (선택)
 * @query maxPrice - 최대 가격 (선택)
 * @query isActive - 활성 상태 필터 (선택)
 */
export const GET = withAuth<GetProductListHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      // URL에서 businessId 추출
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/');
      const businessIdIndex = pathSegments.indexOf('business') + 1;
      const businessId = pathSegments[businessIdIndex];

      // 쿼리 파라미터 추출
      const searchParams = url.searchParams;
      const queryParams = new URLSearchParams();

      const serviceName = searchParams.get('serviceName');
      const businessCategoryId = searchParams.get('businessCategoryId');
      const minPrice = searchParams.get('minPrice');
      const maxPrice = searchParams.get('maxPrice');
      const isActive = searchParams.get('isActive');

      if (serviceName) queryParams.append('serviceName', serviceName);
      if (businessCategoryId)
        queryParams.append('businessCategoryId', businessCategoryId);
      if (minPrice) queryParams.append('minPrice', minPrice);
      if (maxPrice) queryParams.append('maxPrice', maxPrice);
      if (isActive) queryParams.append('isActive', isActive);

      const queryString = queryParams.toString();
      const backendUrl = `${BACKEND_API_URL}/api/business/${businessId}/menu${
        queryString ? `?${queryString}` : ''
      }`;

      console.log('서비스 목록 조회:', { businessId, queryString });

      // 백엔드 API 호출
      const response = await fetch(backendUrl, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
      });

      const result: { data?: ProductListResponse; message?: string } =
        await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: result.message || '서비스 목록 조회에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        data: result.data,
      });
    } catch (error) {
      console.error('서비스 목록 조회 오류:', error);
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
 * 서비스(메뉴) 생성
 * 권한: OWNER, MANAGER만 가능
 *
 * @route POST /api/business/:businessId/menu
 * @param businessId - 업체 ID (UUID)
 * @body CreateProductRequest
 */
export const POST = withAuth<{
  success: boolean;
  data?: any;
  message?: string;
}>(async (request, { accessToken }) => {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessIdIndex = pathSegments.indexOf('business') + 1;
    const businessId = pathSegments[businessIdIndex];

    const body: CreateProductRequest = await request.json();

    console.log('서비스 생성:', { businessId, body });

    const response = await fetch(
      `${BACKEND_API_URL}/api/business/${businessId}/menu`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(body),
      }
    );

    console.log('서비스 생성 응답:', response);
    

    const result: ProductResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: result.message || '서비스 생성에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data,
      message: '서비스가 생성되었습니다.',
    });
  } catch (error) {
    console.error('서비스 생성 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '서버 오류가 발생했습니다.',
      },
      { status: 500 }
    );
  }
});
