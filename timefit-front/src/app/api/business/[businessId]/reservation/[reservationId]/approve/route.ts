import { NextRequest, NextResponse } from 'next/server';

import type {
  BusinessReservationActionApiResponse,
  BusinessReservationActionHandlerResponse,
} from '@/types/business/reservation';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * POST /api/business/[businessId]/reservation/[reservationId]/approve
 * 예약 승인 (PENDING → CONFIRMED)
 */
export async function POST(
  request: NextRequest
): Promise<NextResponse<BusinessReservationActionHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessId = pathSegments[pathSegments.indexOf('business') + 1];
    const reservationId = pathSegments[pathSegments.indexOf('reservation') + 1];

    if (!businessId || !reservationId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID와 예약 ID가 필요합니다.' },
        { status: 400 }
      );
    }

    const body = await request.json().catch(() => ({}));

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/reservation/${reservationId}/approve`,
      {
        method: 'POST',
        body: JSON.stringify(body),
      }
    );

    const responseData: BusinessReservationActionApiResponse =
      await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '예약 승인에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, data: responseData.data });
  } catch (error) {
    return handleApiError<BusinessReservationActionHandlerResponse>(error);
  }
}