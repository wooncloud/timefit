import { NextRequest, NextResponse } from 'next/server';

import type {
  CreateBookingSlotsHandlerResponse,
  CreateBookingSlotsRequest,
  GetBookingSlotsHandlerResponse,
} from '@/types/booking-slot/booking-slot';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

function extractBusinessId(url: URL): string | null {
  const pathSegments = url.pathname.split('/').filter(Boolean);
  const businessIndex = pathSegments.indexOf('business');
  return businessIndex !== -1 ? pathSegments[businessIndex + 1] : null;
}

export async function GET(
  request: NextRequest
): Promise<NextResponse<GetBookingSlotsHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const businessId = extractBusinessId(url);
    const date = url.searchParams.get('date');

    if (!businessId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID가 필요합니다' },
        { status: 400 }
      );
    }

    if (!date) {
      return NextResponse.json(
        { success: false, message: '날짜가 필요합니다' },
        { status: 400 }
      );
    }

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/booking-slot?date=${date}`,
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

export async function POST(
  request: NextRequest
): Promise<NextResponse<CreateBookingSlotsHandlerResponse>> {
  try {
    const url = new URL(request.url);
    const businessId = extractBusinessId(url);

    if (!businessId) {
      return NextResponse.json(
        { success: false, message: '비즈니스 ID가 필요합니다' },
        { status: 400 }
      );
    }

    const body: CreateBookingSlotsRequest = await request.json();

    const response = await apiFetch(
      `${BACKEND_URL}/api/business/${businessId}/booking-slot`,
      {
        method: 'POST',
        body: JSON.stringify(body),
      }
    );

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      return NextResponse.json(
        { success: false, message: errorData.message || '슬롯 생성에 실패했습니다' },
        { status: response.status }
      );
    }

    const result = await response.json();
    return NextResponse.json(
      { success: true, data: result.data, message: '슬롯이 생성되었습니다' },
      { status: 201 }
    );
  } catch (error) {
    return handleApiError<CreateBookingSlotsHandlerResponse>(error);
  }
}
