import { NextResponse } from 'next/server';
import { getServerSession } from '@/lib/session/server';
import type {
  GetMyBusinessApiResponse,
  GetMyBusinessHandlerResponse,
} from '@/types/business/myBusiness';

const BACKEND_API_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

export async function GET(): Promise<NextResponse<GetMyBusinessHandlerResponse>> {
  try {
    const session = await getServerSession();
    const accessToken = session.user?.accessToken;
    const userId = session.user?.userId;

    console.log('Session:', { accessToken: !!accessToken, userId });
    console.log('BACKEND_API_URL:', BACKEND_API_URL);

    if (!accessToken) {
      return NextResponse.json(
        {
          success: false,
          message: '인증이 필요합니다.',
        },
        { status: 401 }
      );
    }

    if (!userId) {
      return NextResponse.json(
        {
          success: false,
          message: '사용자 정보를 찾을 수 없습니다.',
        },
        { status: 401 }
      );
    }

    const url = `${BACKEND_API_URL}/api/businesses/my?CurrentUserId=${userId}`;
    console.log('Fetching:', url);

    const response = await fetch(url, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    const result: GetMyBusinessApiResponse = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        {
          success: false,
          message: result.message || '사업자 목록 조회 실패',
        },
        { status: response.status }
      );
    }

    return NextResponse.json({
      success: true,
      data: result.data || [],
    });
  } catch (error) {
    console.error('사업자 목록 조회 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '서버 오류가 발생했습니다.',
      },
      { status: 500 }
    );
  }
}
