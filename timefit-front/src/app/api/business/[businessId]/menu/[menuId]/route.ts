import { NextRequest, NextResponse } from 'next/server';

import type {
  CreateUpdateMenuRequest,
  DeleteMenuHandlerResponse,
  GetMenuDetailHandlerResponse,
  UpdateMenuHandlerResponse,
} from '@/types/menu/menu';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetMenuDetailHandlerResponse>> {
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

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
      { method: 'GET' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '메뉴를 불러오는데 실패했습니다',
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
    return handleApiError<GetMenuDetailHandlerResponse>(error);
  }
}

export async function PATCH(
  request: NextRequest
): Promise<NextResponse<UpdateMenuHandlerResponse>> {
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

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
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
          message: errorData.message || '메뉴 수정에 실패했습니다',
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
    return handleApiError<UpdateMenuHandlerResponse>(error);
  }
}

export async function DELETE(
  request: NextRequest
): Promise<NextResponse<DeleteMenuHandlerResponse>> {
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

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/menu/${menuId}`,
      { method: 'DELETE' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '메뉴 삭제에 실패했습니다',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      message: '메뉴가 삭제되었습니다',
    });
  } catch (error) {
    return handleApiError<DeleteMenuHandlerResponse>(error);
  }
}
