import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';

import { SessionData, sessionOptions } from '@/lib/session/options';

export async function POST(request: NextRequest) {
  try {
    const responseJson = NextResponse.json(
      {
        success: true,
        message: '로그아웃되었습니다.',
      },
      { status: 200 }
    );

    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );

    session.destroy();
    await session.save(); // 쿠키 삭제를 위해 저장 필요

    return responseJson;
  } catch (error) {
    console.error('로그아웃 오류:', error);
    return NextResponse.json(
      {
        success: false,
        message: '로그아웃에 실패했습니다.',
      },
      { status: 500 }
    );
  }
}
