import { NextResponse } from 'next/server';

import type {
  CreateCategoryHandlerResponse,
  CreateCategoryRequest,
  GetCategoryListHandlerResponse,
} from '@/types/category/category';
import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL is not defined');
}

export const GET = withAuth<GetCategoryListHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const businessId =
        businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;

      if (!businessId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const backendUrl = `${BACKEND_URL}/api/business/${businessId}/category`;

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
            message:
              errorData.message || '카테고리 목록을 불러오는데 실패했습니다',
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
      console.error('Category list fetch error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '카테고리 목록을 불러오는 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);

export const POST = withAuth<CreateCategoryHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/').filter(Boolean);
      const businessIndex = pathSegments.indexOf('business');
      const businessId =
        businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;

      if (!businessId) {
        return NextResponse.json(
          {
            success: false,
            message: '비즈니스 ID가 필요합니다',
          },
          { status: 400 }
        );
      }

      const body: CreateCategoryRequest = await request.json();

      const backendUrl = `${BACKEND_URL}/api/business/${businessId}/category`;
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
            message: errorData.message || '카테고리 생성에 실패했습니다',
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
          message: '카테고리가 생성되었습니다',
        },
        { status: 201 }
      );
    } catch (error) {
      console.error('Category creation error:', error);
      return NextResponse.json(
        {
          success: false,
          message: '카테고리 생성 중 오류가 발생했습니다',
        },
        { status: 500 }
      );
    }
  }
);
