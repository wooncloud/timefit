import { NextResponse } from 'next/server';

import { withAuth } from '@/lib/api/auth-middleware';
import type { ToggleOperatingHoursHandlerResponse } from '@/types/business/operating-hours';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * [PATCH] 운영 시간 토글 (활성화/비활성화)
 */
export const PATCH = withAuth<ToggleOperatingHoursHandlerResponse>(
  async (request, { accessToken }) => {
    try {
      // URL에서 businessId와 dayOfWeek 추출
      const url = new URL(request.url);
      const pathSegments = url.pathname.split('/');
      const businessId = pathSegments[pathSegments.indexOf('business') + 1];
      const dayOfWeekIndex = pathSegments.indexOf('operating-hours') + 1;
      const dayOfWeek = pathSegments[dayOfWeekIndex];

      if (!businessId || !dayOfWeek) {
        return NextResponse.json(
          {
            success: false,
            message: '업체 ID 또는 요일 정보가 누락되었습니다.',
          },
          { status: 400 }
        );
      }

      const response = await fetch(
        `${BACKEND_URL}/api/business/${businessId}/operating-hours/${dayOfWeek}/toggle`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }
      );

      const result = await response.json();

      if (!response.ok) {
        return NextResponse.json(
          {
            success: false,
            message: result.message || '운영 시간 토글에 실패했습니다.',
          },
          { status: response.status }
        );
      }

      return NextResponse.json({
        success: true,
        data: result.data,
        message: result.message,
      });
    } catch (error) {
      console.error('운영 시간 토글 API 오류:', error);
      return NextResponse.json(
        {
          success: false,
          message: '운영 시간 토글 중 서버 오류가 발생했습니다.',
        },
        { status: 500 }
      );
    }
  }
);
