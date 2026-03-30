import { NextRequest, NextResponse } from 'next/server';

import type {
  GetBusinessReservationListApiResponse,
  GetBusinessReservationListHandlerResponse,
} from '@/types/business/reservation';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * GET /api/business/[businessId]/reservations
 * 사업자용 예약 목록 조회
 */
export async function GET(
  request: NextRequest
): Promise<NextResponse<GetBusinessReservationListHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    const businessId = pathSegments[pathSegments.indexOf('business') + 1];

    if (!businessId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID가 필요합니다.' },
        { status: 400 }
      );
    }

    const queryString = url.searchParams.toString();
    const backendUrl = `${BACKEND_API_URL}/api/business/${businessId}/reservations${queryString ? `?${queryString}` : ''}`;

    const response = await apiFetch(backendUrl, { method: 'GET' });
    const responseData: GetBusinessReservationListApiResponse =
      await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '예약 목록 조회에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, data: responseData.data });
  } catch (error) {
    return handleApiError<GetBusinessReservationListHandlerResponse>(error);
  }
}