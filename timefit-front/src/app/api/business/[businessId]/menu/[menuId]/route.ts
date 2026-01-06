import { NextResponse } from 'next/server';

import type {
  CreateUpdateMenuRequest,
  DeleteMenuHandlerResponse,
  GetMenuDetailHandlerResponse,
  UpdateMenuHandlerResponse,
} from '@/types/menu/menu';
import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

export const GET = withAuth<GetMenuDetailHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const menuIndex = pathSegments.indexOf('menu');

      const businessId =
        businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
      const menuId = menuIndex !== -1 ? pathSegments[menuIndex + 1] : null;

      if (!businessId || !menuId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID와 메뉴 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const response = await fetch(
        `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
        {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        return NextResponse.json(
          {
            success: false,
            message: errorData.message || '메뉴를 불러오는데 실패했습니다',
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
      console.error('메뉴 상세 조회 오류:', error);
      return NextResponse.json(
        {
          success: false,
          message: '메뉴를 불러오는 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);

export const PATCH = withAuth<UpdateMenuHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const menuIndex = pathSegments.indexOf('menu');

      const businessId =
        businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
      const menuId = menuIndex !== -1 ? pathSegments[menuIndex + 1] : null;

      if (!businessId || !menuId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID와 메뉴 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const body: CreateUpdateMenuRequest = await request.json();

      const response = await fetch(
        `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(body),
        }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        return NextResponse.json(
          {
            success: false,
            message: errorData.message || '메뉴 수정에 실패했습니다',
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
        message: '메뉴가 수정되었습니다',
      });
    } catch (error) {
      console.error('메뉴 수정 오류:', error);
      return NextResponse.json(
        {
          success: false,
          message: '메뉴 수정 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);

export const DELETE = withAuth<DeleteMenuHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const menuIndex = pathSegments.indexOf('menu');

      const businessId =
        businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
      const menuId = menuIndex !== -1 ? pathSegments[menuIndex + 1] : null;

      if (!businessId || !menuId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID와 메뉴 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const response = await fetch(
        `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
        {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        return NextResponse.json(
          {
            success: false,
            message: errorData.message || '메뉴 삭제에 실패했습니다',
            requiresLogout: response.status === 401,
            redirectTo: response.status === 401 ? '/' : undefined,
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        message: '메뉴가 삭제되었습니다',
      });
    } catch (error) {
      console.error('메뉴 삭제 오류:', error);
      return NextResponse.json(
        {
          success: false,
          message: '메뉴 삭제 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);
