import { NextRequest, NextResponse } from 'next/server';
import { withAuth } from '@/lib/api/auth-middleware';
import type {
  UpdateCategoryHandlerResponse,
  DeleteCategoryHandlerResponse,
  UpdateCategoryRequest,
} from '@/types/category/category';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL is not defined');
}

export const PATCH = withAuth<UpdateCategoryHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const categoryIndex = pathSegments.indexOf('category');
      
      const businessId = businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
      const categoryId = categoryIndex !== -1 ? pathSegments[categoryIndex + 1] : null;

      if (!businessId || !categoryId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID와 카테고리 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const body: UpdateCategoryRequest = await request.json();

      const response = await fetch(
        `${BACKEND_URL}/api/business/${businessId}/category/${categoryId}`,
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
            message: errorData.message || '카테고리 수정에 실패했습니다',
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
        message: '카테고리가 수정되었습니다',
      });
    } catch (error) {
      console.error('Category update error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '카테고리 수정 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);

export const DELETE = withAuth<DeleteCategoryHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const categoryIndex = pathSegments.indexOf('category');
      
      const businessId = businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
      const categoryId = categoryIndex !== -1 ? pathSegments[categoryIndex + 1] : null;

      if (!businessId || !categoryId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID와 카테고리 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const response = await fetch(
        `${BACKEND_URL}/api/business/${businessId}/category/${categoryId}`,
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
            message: errorData.message || '카테고리 삭제에 실패했습니다',
            requiresLogout: response.status === 401,
            redirectTo: response.status === 401 ? '/' : undefined,
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        message: '카테고리가 삭제되었습니다',
      });
    } catch (error) {
      console.error('Category delete error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '카테고리 삭제 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);

