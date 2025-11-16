import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';
import { sessionOptions, SessionData } from '@/lib/session/options';

export async function POST(request: NextRequest) {
  try {
    const responseJson = NextResponse.json({
      success: true,
      message: '로그아웃되었습니다.',
    });

    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );

    session.destroy();

    return responseJson;
  } catch (error) {
    console.error('로그아웃 API 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '로그아웃 처리 중 오류가 발생했습니다.',
      },
      { status: 500 }
    );
  }
}
