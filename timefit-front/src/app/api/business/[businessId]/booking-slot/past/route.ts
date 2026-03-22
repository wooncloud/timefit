import { NextRequest, NextResponse } from 'next/server';

import type { DeletePastSlotsHandlerResponse } from '@/types/booking-slot/booking-slot';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function DELETE(
  request: NextRequest
): Promise<NextResponse<DeletePastSlotsHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const businessIndex = pathSegments.indexOf('business');
    const businessId =
      businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;

    if (!businessId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID가 필요합니다' },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/booking-slot/past`,
      { method: 'DELETE' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        { success: false, message: errorData.message || '과거 슬롯 삭제에 실패했습니다' },
        { status: response.status }
      );
    }

    const result = await response.json();
    return NextResponse.json({
      success: true,
      data: result.data,
      message: '과거 슬롯이 삭제되었습니다',
    });
  } catch (error) {
    return handleApiError<DeletePastSlotsHandlerResponse>(error);
  }
}
