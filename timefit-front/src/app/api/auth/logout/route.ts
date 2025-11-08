import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';
import { sessionOptions, SessionData } from '@/lib/session/options';
import { clearAccessTokenCookie } from '@/lib/cookie';

export async function POST(request: NextRequest) {
  try {
    const responseJson = NextResponse.json(
      {
        success: true,
        message: 'Logged out successfully',
      },
      { status: 200 }
    );

    const session = await getIronSession<SessionData>(
      request,
      responseJson,
      sessionOptions
    );

    session.destroy();

    await clearAccessTokenCookie();

    return responseJson;
  } catch (error) {
    console.error('Logout error:', error);
    return NextResponse.json(
      {
        success: false,
        message: 'Failed to logout',
      },
      { status: 500 }
    );
  }
}

