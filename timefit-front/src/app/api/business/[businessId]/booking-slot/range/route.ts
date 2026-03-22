import { NextRequest, NextResponse } from 'next/server';

import type { GetBookingSlotsHandlerResponse } from '@/types/booking-slot/booking-slot';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetBookingSlotsHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/').filter(Boolean);
    const businessIndex = pathSegments.indexOf('business');
    const businessId =
      businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
    const startDate = url.searchParams.get('startDate');
    const endDate = url.searchParams.get('endDate');

    if (!businessId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID가 필요합니다' },
        { status: 400 }
      );
    }

    if (!startDate || !endDate) {
      return NextResponse.json(
        { success: false, message: '시작/종료 날짜가 필요합니다' },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/booking-slot/range?startDate=${startDate}&endDate=${endDate}`,
      { method: 'GET' }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        { success: false, message: errorData.message || '슬롯 조회에 실패했습니다' },
        { status: response.status }
      );
    }

    const result = await response.json();
    return NextResponse.json({ success: true, data: result.data });
  } catch (error) {
    return handleApiError<GetBookingSlotsHandlerResponse>(error);
  }
}
