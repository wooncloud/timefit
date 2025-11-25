import { NextRequest, NextResponse } from 'next/server';
import { withAuth } from '@/lib/api/auth-middleware';
import type { UpdateMenuHandlerResponse } from '@/types/menu/menu';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL is not defined');
}

export const PATCH = withAuth<UpdateMenuHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const menuIndex = pathSegments.indexOf('menu');
      
      const businessId = businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
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
        `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}/toggle`,
        {
          method: 'PATCH',
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
            message: errorData.message || '메뉴 상태 변경에 실패했습니다',
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
        message: '메뉴 상태가 변경되었습니다',
      });
    } catch (error) {
      console.error('Menu toggle error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '메뉴 상태 변경 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);
