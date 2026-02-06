import { NextRequest, NextResponse } from 'next/server';

import type { AddWishlistHandlerResponse } from '@/types/customer/wishlist';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function POST(
  request: NextRequest
): Promise<NextResponse<AddWishlistHandlerResponse>> {
  try {
    const body = await request.json();

    if (!body.menuId) {
      return NextResponse.json(
        { success: false, message: '메뉴 ID가 필요합니다.' },
        { status: 400 }
      );
    }

    const response = await apiFetch(`${BACKEND_URL}/api/customer/wishlist`, {
      method: 'POST',
      body: JSON.stringify(body),
    });

    const responseData = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '찜 추가에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: responseData.data,
      message: responseData.message || '찜 목록에 추가되었습니다.',
    });
  } catch (error) {
    return handleApiError<AddWishlistHandlerResponse>(error);
  }
}
