import { NextRequest, NextResponse } from 'next/server';

import type {
  GetBusinessCustomerDetailApiResponse,
  GetBusinessCustomerDetailHandlerResponse,
} from '@/types/business/customer-business';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * GET /api/business/[businessId]/customers/[customerId]
 * 사업자용 고객 상세 조회
 */
export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ businessId: string; customerId: string }> }
): Promise<NextResponse<GetBusinessCustomerDetailHandlerResponse>> {
  try {
    const { businessId, customerId } = await params;

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/customers/${customerId}`,
      { method: 'GET' }
    );

    const responseData: GetBusinessCustomerDetailApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        { success: false, message: responseData.message || '고객 상세 조회에 실패했습니다.' },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, data: responseData.data });
  } catch (error) {
    return handleApiError<GetBusinessCustomerDetailHandlerResponse>(error);
  }
}