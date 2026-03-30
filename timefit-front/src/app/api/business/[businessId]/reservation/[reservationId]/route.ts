import { NextRequest, NextResponse } from 'next/server';

import type {
  GetBusinessReservationDetailApiResponse,
  GetBusinessReservationDetailHandlerResponse,
} from '@/types/business/reservation';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * GET /api/business/[businessId]/reservation/[reservationId]
 * 사업자용 예약 상세 조회
 */
export async function GET(
  request: NextRequest
): Promise<NextResponse<GetBusinessReservationDetailHandlerResponse>> {
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

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/reservation/${reservationId}`,
      { method: 'GET' }
    );

    const responseData: GetBusinessReservationDetailApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '예약 상세 조회에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, data: responseData.data });
  } catch (error) {
    return handleApiError<GetBusinessReservationDetailHandlerResponse>(error);
  }
}