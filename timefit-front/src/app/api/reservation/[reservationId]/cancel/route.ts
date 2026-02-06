import { NextRequest, NextResponse } from 'next/server';

import type { CancelReservationHandlerResponse } from '@/types/customer/reservation';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

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

    const responseData = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: responseData.message || '예약 취소에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: responseData.data,
      message: responseData.message || '예약이 취소되었습니다.',
    });
  } catch (error) {
    return handleApiError<CancelReservationHandlerResponse>(error);
  }
}
