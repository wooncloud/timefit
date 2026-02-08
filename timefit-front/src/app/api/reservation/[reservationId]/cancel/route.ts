import { NextRequest, NextResponse } from 'next/server';

import type {
  CancelReservationApiResponse,
  CancelReservationHandlerResponse,
} from '@/types/customer/reservation';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

/**
 * POST /api/reservation/[reservationId]/cancel - 예약 취소
 */
export async function POST(
  request: NextRequest,
  { params }: { params: Promise<{ reservationId: string }> }
): Promise<NextResponse<CancelReservationHandlerResponse>> {
  try {
    const { reservationId } = await params;
    const body = await request.json();

    const response = await apiFetch(
      `${BACKEND_URL}/api/reservation/${reservationId}/cancel`,
      {
        method: 'POST',
        body: JSON.stringify(body),
      }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '예약 취소에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    const result: CancelReservationApiResponse = await response.json();

    if (!result.data) {
      return NextResponse.json(
        {
          success: false,
          message: '예약 취소 데이터를 찾을 수 없습니다.',
        },
        { status: 500 }
      );
    }

    return NextResponse.json({
      success: true,
      data: {
        reservationId: result.data.reservationId,
        message: result.data.message,
      },
    });
  } catch (error) {
    return handleApiError<CancelReservationHandlerResponse>(error);
  }
}
