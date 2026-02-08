import { NextRequest, NextResponse } from 'next/server';

import type {
  CreateReservationApiResponse,
  CreateReservationHandlerResponse,
  CreateReservationRequest,
} from '@/types/customer/reservation';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

/**
 * POST /api/reservation - 예약 생성
 */
export async function POST(
  request: NextRequest
): Promise<NextResponse<CreateReservationHandlerResponse>> {
  try {
    const body: CreateReservationRequest = await request.json();

    const response = await apiFetch(`${BACKEND_URL}/api/reservation`, {
      method: 'POST',
      body: JSON.stringify(body),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        {
          success: false,
          message: errorData.message || '예약 생성에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    const result: CreateReservationApiResponse = await response.json();

    if (!result.data) {
      return NextResponse.json(
        {
          success: false,
          message: '예약 데이터를 찾을 수 없습니다.',
        },
        { status: 500 }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data,
    });
  } catch (error) {
    return handleApiError<CreateReservationHandlerResponse>(error);
  }
}
