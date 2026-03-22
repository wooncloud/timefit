import { NextRequest, NextResponse } from 'next/server';

import type { ToggleBookingSlotHandlerResponse } from '@/types/booking-slot/booking-slot';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function PATCH(
  request: NextRequest
): Promise<NextResponse<ToggleBookingSlotHandlerResponse>> {
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
      `${BACKEND_URL}/api/business/${businessId}/booking-slot/${slotId}/activate`,
      { method: 'PATCH' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        { success: false, message: errorData.message || '슬롯 활성화에 실패했습니다' },
        { status: response.status }
      );
    }

    const result = await response.json();
    return NextResponse.json({ success: true, data: result.data });
  } catch (error) {
    return handleApiError<ToggleBookingSlotHandlerResponse>(error);
  }
}
