import { NextRequest, NextResponse } from 'next/server';

import type { DeleteWishlistHandlerResponse } from '@/types/customer/wishlist';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function DELETE(
  request: NextRequest,
  { params }: { params: Promise<{ menuId: string }> }
): Promise<NextResponse<DeleteWishlistHandlerResponse>> {
  try {
    const { menuId } = await params;

    const response = await apiFetch(
      `${BACKEND_URL}/api/customer/wishlist/${menuId}`,
      { method: 'DELETE' }
    );

    const responseData = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '찜 삭제에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: responseData.data,
      message: responseData.message || '찜 목록에서 제거되었습니다.',
    });
  } catch (error) {
    return handleApiError<DeleteWishlistHandlerResponse>(error);
  }
}
