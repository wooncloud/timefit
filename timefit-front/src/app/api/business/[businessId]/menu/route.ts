import { NextRequest, NextResponse } from 'next/server';
import { withAuth } from '@/lib/api/auth-middleware';
import type {
  GetMenuListHandlerResponse,
  CreateMenuHandlerResponse,
  CreateUpdateMenuRequest,
} from '@/types/menu/menu';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL is not defined');
}

export const GET = withAuth<GetMenuListHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const { searchParams } = url;
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const businessId = businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;

      if (!businessId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const serviceName = searchParams.get('serviceName');
      const businessCategoryId = searchParams.get('businessCategoryId');
      const minPrice = searchParams.get('minPrice');
      const maxPrice = searchParams.get('maxPrice');
      const isActive = searchParams.get('isActive');

      const queryParams = new URLSearchParams();
      if (serviceName) queryParams.append('serviceName', serviceName);
      if (businessCategoryId) queryParams.append('businessCategoryId', businessCategoryId);
      if (minPrice) queryParams.append('minPrice', minPrice);
      if (maxPrice) queryParams.append('maxPrice', maxPrice);
      if (isActive !== null && isActive !== undefined) {
        queryParams.append('isActive', isActive);
      }

      const queryString = queryParams.toString();
      const backendUrl = `${BACKEND_URL}/api/business/${businessId}/menu${queryString ? `?${queryString}` : ''}`;

      const response = await fetch(backendUrl, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        return NextResponse.json(
          {
            success: false,
            message: errorData.message || '메뉴 목록을 불러오는데 실패했습니다',
            requiresLogout: response.status === 401,
            redirectTo: response.status === 401 ? '/' : undefined,
          },
          { status: response.status }
        );
      }

      const result = await response.json();

      return NextResponse.json({
        success: true,
        data: result.data,
      });
    } catch (error) {
      console.error('Menu list fetch error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '메뉴 목록을 불러오는 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);

export const POST = withAuth<CreateMenuHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const businessId = businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;

      if (!businessId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const body: CreateUpdateMenuRequest = await request.json();

      const backendUrl = `${BACKEND_URL}/api/business/${businessId}/menu`;
      const response = await fetch(backendUrl, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        return NextResponse.json(
          {
            success: false,
            message: errorData.message || '메뉴 생성에 실패했습니다',
            requiresLogout: response.status === 401,
            redirectTo: response.status === 401 ? '/' : undefined,
          },
          { status: response.status }
        );
      }

      const result = await response.json();

      return NextResponse.json(
        {
          success: true,
          data: result.data,
          message: '메뉴가 생성되었습니다',
        },
        { status: 201 }
      );
    } catch (error) {
      console.error('Menu creation error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '메뉴 생성 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);
