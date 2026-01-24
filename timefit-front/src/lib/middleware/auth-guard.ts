import { NextRequest, NextResponse } from 'next/server';
import { getIronSession } from 'iron-session';

import { sessionOptions } from '@/lib/session/options';
import type { SessionData } from '@/lib/session/options';

import {
  isProtectedBusinessPath,
  isProtectedCustomerPath,
} from './protected-paths';

interface AuthGuardResult {
  isAuthenticated: boolean;
  response: NextResponse;
  redirectResponse?: NextResponse;
}

/**
 * 고객 페이지 인증 가드
 * 로그인이 필요한 고객 페이지에 대해 인증을 체크하고,
 * 미인증 시 로그인 페이지로 리다이렉트
 */
export async function customerAuthGuard(
  request: NextRequest
): Promise<AuthGuardResult> {
  const pathname = request.nextUrl.pathname;
  const response = NextResponse.next();

  // 보호된 고객 경로가 아니면 통과
  if (!isProtectedCustomerPath(pathname)) {
    return { isAuthenticated: true, response };
  }

  const session = await getIronSession<SessionData>(
    request,
    response,
    sessionOptions
  );

  // 로그인이 안 되어 있으면 로그인 페이지로 리다이렉트
  if (!session.user?.accessToken) {
    const signinUrl = new URL('/signin', request.url);
    signinUrl.searchParams.set('callbackUrl', pathname);
    return {
      isAuthenticated: false,
      response,
      redirectResponse: NextResponse.redirect(signinUrl),
    };
  }

  return { isAuthenticated: true, response };
}

/**
 * 비즈니스 페이지 인증 가드
 * 로그인이 필요한 비즈니스 페이지에 대해 인증을 체크하고,
 * 미인증 시 비즈니스 로그인 페이지로 리다이렉트
 */
export async function businessAuthGuard(
  request: NextRequest
): Promise<AuthGuardResult> {
  const pathname = request.nextUrl.pathname;
  const response = NextResponse.next();

  // 보호된 비즈니스 경로가 아니면 통과
  if (!isProtectedBusinessPath(pathname)) {
    return { isAuthenticated: true, response };
  }

  const session = await getIronSession<SessionData>(
    request,
    response,
    sessionOptions
  );

  // 로그인이 안 되어 있으면 비즈니스 로그인 페이지로 리다이렉트
  if (!session.user?.accessToken) {
    const signinUrl = new URL('/business/signin', request.url);
    signinUrl.searchParams.set('callbackUrl', pathname);
    return {
      isAuthenticated: false,
      response,
      redirectResponse: NextResponse.redirect(signinUrl),
    };
  }

  return { isAuthenticated: true, response };
}
