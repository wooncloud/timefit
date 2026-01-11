import { NextRequest, NextResponse } from 'next/server';

import type {
  CreateMenuHandlerResponse,
  CreateUpdateMenuRequest,
  GetMenuListHandlerResponse,
} from '@/types/menu/menu';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetMenuListHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const { searchParams } = url;
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

    const serviceName = searchParams.get('serviceName');
    const businessCategoryId = searchParams.get('businessCategoryId');
    const minPrice = searchParams.get('minPrice');
    const maxPrice = searchParams.get('maxPrice');
    const isActive = searchParams.get('isActive');

    const queryParams = new URLSearchParams();
    if (serviceName) queryParams.append('serviceName', serviceName);
    if (businessCategoryId)
      queryParams.append('businessCategoryId', businessCategoryId);
    if (minPrice) queryParams.append('minPrice', minPrice);
    if (maxPrice) queryParams.append('maxPrice', maxPrice);
    if (isActive !== null && isActive !== undefined) {
      queryParams.append('isActive', isActive);
    }

    const queryString = queryParams.toString();
    const backendUrl = `${BACKEND_URL}/api/business/${businessId}/menu${queryString ? `?${queryString}` : ''}`;

    const response = await apiFetch(backendUrl, { method: 'GET' });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '메뉴 목록을 불러오는데 실패했습니다',
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
    return handleApiError<GetMenuListHandlerResponse>(error);
  }
}

export async function POST(
  request: NextRequest
): Promise<NextResponse<CreateMenuHandlerResponse>> {
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

    const body: CreateUpdateMenuRequest = await request.json();

    const backendUrl = `${BACKEND_URL}/api/business/${businessId}/menu`;
    const response = await apiFetch(backendUrl, {
      method: 'POST',
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '메뉴 생성에 실패했습니다',
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
    return handleApiError<CreateMenuHandlerResponse>(error);
  }
}
