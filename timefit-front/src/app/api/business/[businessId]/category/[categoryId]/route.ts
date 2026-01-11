import { NextRequest, NextResponse } from 'next/server';

import type {
  DeleteCategoryHandlerResponse,
  GetCategoryDetailHandlerResponse,
  UpdateCategoryHandlerResponse,
  UpdateCategoryRequest,
} from '@/types/category/category';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetCategoryDetailHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const businessIndex = pathSegments.indexOf('business');
    const categoryIndex = pathSegments.indexOf('category');

    const businessId =
      businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
    const categoryId =
      categoryIndex !== -1 ? pathSegments[categoryIndex + 1] : null;

    if (!businessId || !categoryId) {
      return NextResponse.json(
        {
          success: false,
          message: '비즈니스 ID와 카테고리 ID가 필요합니다',
        },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/category/${categoryId}`,
      { method: 'GET' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '카테고리 조회에 실패했습니다',
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
    return handleApiError<GetCategoryDetailHandlerResponse>(error);
  }
}

export async function PATCH(
  request: NextRequest
): Promise<NextResponse<UpdateCategoryHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const businessIndex = pathSegments.indexOf('business');
    const categoryIndex = pathSegments.indexOf('category');

    const businessId =
      businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
    const categoryId =
      categoryIndex !== -1 ? pathSegments[categoryIndex + 1] : null;

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

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/category/${categoryId}`,
      {
        method: 'PATCH',
        body: JSON.stringify(body),
      }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '카테고리 수정에 실패했습니다',
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
    return handleApiError<UpdateCategoryHandlerResponse>(error);
  }
}

export async function DELETE(
  request: NextRequest
): Promise<NextResponse<DeleteCategoryHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const businessIndex = pathSegments.indexOf('business');
    const categoryIndex = pathSegments.indexOf('category');

    const businessId =
      businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
    const categoryId =
      categoryIndex !== -1 ? pathSegments[categoryIndex + 1] : null;

    if (!businessId || !categoryId) {
      return NextResponse.json(
        {
          success: false,
          message: '비즈니스 ID와 카테고리 ID가 필요합니다',
        },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/category/${categoryId}`,
      { method: 'DELETE' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '카테고리 삭제에 실패했습니다',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      message: '카테고리가 삭제되었습니다',
    });
  } catch (error) {
    return handleApiError<DeleteCategoryHandlerResponse>(error);
  }
}
