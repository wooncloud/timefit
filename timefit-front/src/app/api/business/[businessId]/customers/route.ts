import { NextRequest, NextResponse } from 'next/server';

import type {
  GetBusinessCustomerListApiResponse,
  GetBusinessCustomerListHandlerResponse,
} from '@/types/business/customer-business';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * GET /api/business/[businessId]/customers
 * 사업자용 고객 목록 조회
 */
export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ businessId: string }> }
): Promise<NextResponse<GetBusinessCustomerListHandlerResponse>> {
  try {
    const { businessId } = await params;
    const url = new URL(request.url);
    const queryString = url.searchParams.toString();

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/customers${queryString ? `?${queryString}` : ''}`,
      { method: 'GET' }
    );

    const responseData: GetBusinessCustomerListApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        { success: false, message: responseData.message || '고객 목록 조회에 실패했습니다.' },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, data: responseData.data });
  } catch (error) {
    return handleApiError<GetBusinessCustomerListHandlerResponse>(error);
  }
}