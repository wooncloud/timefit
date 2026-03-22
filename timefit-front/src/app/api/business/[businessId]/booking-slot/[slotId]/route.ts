import { NextRequest, NextResponse } from 'next/server';

import type { DeleteBookingSlotHandlerResponse } from '@/types/booking-slot/booking-slot';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function DELETE(
  request: NextRequest
): Promise<NextResponse<DeleteBookingSlotHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const businessIndex = pathSegments.indexOf('business');
    const businessId =
      businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
    const slotIndex = pathSegments.indexOf('booking-slot');
    const slotId =
      slotIndex !== -1 ? pathSegments[slotIndex + 1] : null;

    if (!businessId || !slotId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID와 슬롯 ID가 필요합니다' },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/booking-slot/${slotId}`,
      { method: 'DELETE' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        { success: false, message: errorData.message || '슬롯 삭제에 실패했습니다' },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, message: '슬롯이 삭제되었습니다' });
  } catch (error) {
    return handleApiError<DeleteBookingSlotHandlerResponse>(error);
  }
}
