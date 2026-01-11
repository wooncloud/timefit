import { NextRequest, NextResponse } from 'next/server';

import type {
  CreateCategoryHandlerResponse,
  CreateCategoryRequest,
  GetCategoryListHandlerResponse,
} from '@/types/category/category';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetCategoryListHandlerResponse>> {
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

    const backendUrl = `${BACKEND_URL}/api/business/${businessId}/categories`;

    const response = await apiFetch(backendUrl, { method: 'GET' });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message:
            errorData.message || '카테고리 목록을 불러오는데 실패했습니다',
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
    return handleApiError<GetCategoryListHandlerResponse>(error);
  }
}

export async function POST(
  request: NextRequest
): Promise<NextResponse<CreateCategoryHandlerResponse>> {
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
    const response = await apiFetch(backendUrl, {
      method: 'POST',
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '카테고리 생성에 실패했습니다',
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
    return handleApiError<CreateCategoryHandlerResponse>(error);
  }
}
