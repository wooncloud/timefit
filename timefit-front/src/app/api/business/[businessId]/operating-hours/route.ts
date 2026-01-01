import { NextResponse } from 'next/server';

import { withAuth } from '@/lib/api/auth-middleware';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export const PUT = withAuth(async (request, { accessToken }) => {
  try {
    // URL에서 businessId 추출 (예: /api/business/[businessId]/operating-hours)
    const url = new URL(request.url);
    const pathSegments = url.pathname.split('/');
    // operating-hours 바로 앞의 세그먼트가 businessId
    const businessId = pathSegments[pathSegments.length - 2];

    const body = await request.json();

    const response = await fetch(
      `${BACKEND_API_URL}/api/business/${businessId}/operating-hours`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`,
        },
        body: JSON.stringify(body),
      }
    );

    if (!response.ok) {
      let errorMessage = '영업시간 수정에 실패했습니다.';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch (_e) {
        // 응답이 JSON이 아닐 경우 기본 메시지 사용
      }

      return NextResponse.json(
        {
          success: false,
          message: errorMessage,
        },
        { status: response.status }
      );
    }

    const result = await response.json();

    return NextResponse.json({
      success: true,
      data: result.data,
      message: '영업시간이 수정되었습니다.',
    });
  } catch (error) {
    console.error('영업시간 수정 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '서버 오류가 발생했습니다.',
      },
      { status: 500 }
    );
  }
});
