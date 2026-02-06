import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';

import { SessionData, sessionOptions } from '@/lib/session/options';

const BACKEND_API_URL =
    process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

/**
 * 비밀번호 변경 (PUT /api/customer/profile/password)
 */
export async function PUT(request: NextRequest) {
    try {
        // 세션에서 accessToken 가져오기
        const session = await getIronSession<SessionData>(
            request,
            NextResponse.next(),
            sessionOptions
        );

        if (!session.user?.accessToken) {
            return NextResponse.json(
                {
                    success: false,
                    message: '인증되지 않은 요청입니다.',
                },
                { status: 401 }
            );
        }

        // 요청 본문 가져오기
        const body = await request.json();

        // 백엔드로 요청 프록시
        const response = await fetch(
            `${BACKEND_API_URL}/api/customer/profile/password`,
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${session.user.accessToken}`,
                },
                body: JSON.stringify(body),
            }
        );

        const data = await response.json();

        if (!response.ok) {
            return NextResponse.json(data, { status: response.status });
        }

        return NextResponse.json(data);
    } catch (error) {
        console.error('비밀번호 변경 오류:', error);
        return NextResponse.json(
            {
                success: false,
                message: '서버 오류가 발생했습니다.',
            },
            { status: 500 }
        );
    }
}
