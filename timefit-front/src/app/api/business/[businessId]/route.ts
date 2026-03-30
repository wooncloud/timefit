import { NextRequest, NextResponse } from 'next/server';

import type {
  UpdateBusinessApiResponse,
  UpdateBusinessHandlerResponse,
  UpdateBusinessRequest,
} from '@/types/business/business-detail';
import { apiFetch } from '@/lib/api/api-fetch';
import { handleApiError } from '@/lib/api/error-handler';

const BACKEND_API_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * PUT /api/business/[businessId]
 * 업체 정보 수정
 */
export async function PUT(
  request: NextRequest,
  { params }: { params: Promise<{ businessId: string }> }
): Promise<NextResponse<UpdateBusinessHandlerResponse>> {
  try {
    const { businessId } = await params;

    const body: UpdateBusinessRequest = await request.json();

    const response = await apiFetch(
      `${BACKEND_API_URL}/api/business/${businessId}`,
      {
        method: 'PUT',
        body: JSON.stringify(body),
      }
    );

    const result: UpdateBusinessApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: result.message || '업체 정보 수정에 실패했습니다.',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data,
      message: '업체 정보가 수정되었습니다.',
    });
  } catch (error) {
    return handleApiError<UpdateBusinessHandlerResponse>(error);
  }
}