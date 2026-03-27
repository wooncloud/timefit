import { NextRequest, NextResponse } from 'next/server';

import type {
  UpsertCustomerMemoApiResponse,
  UpsertCustomerMemoHandlerResponse,
} from '@/types/business/customer-business';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * PATCH /api/business/[businessId]/customers/[customerId]/memo
 * 고객 메모 저장/수정
 */
export async function PATCH(
  request: NextRequest,
  { params }: { params: Promise<{ businessId: string; customerId: string }> }
): Promise<NextResponse<UpsertCustomerMemoHandlerResponse>> {
  try {
    const { businessId, customerId } = await params;
    const body = await request.json();

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}/customers/${customerId}/memo`,
      {
        method: 'PATCH',
        body: JSON.stringify(body),
      }
    );

    const responseData: UpsertCustomerMemoApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        { success: false, message: responseData.message || '메모 저장에 실패했습니다.' },
        { status: response.status }
      );
    }

    return NextResponse.json({ success: true, data: responseData.data });
  } catch (error) {
    return handleApiError<UpsertCustomerMemoHandlerResponse>(error);
  }
}