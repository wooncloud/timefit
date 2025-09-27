import { cookies } from 'next/headers';

export const ACCESS_TOKEN_COOKIE_NAME = 'accessToken';
export const ACCESS_TOKEN_MAX_AGE = 60 * 15; // 15 minutes

const isProduction = process.env.NODE_ENV === 'production';

interface SetAccessTokenOptions {
  maxAge?: number;
}

/**
 * 액세스 토큰을 공용 쿠키로 저장.
 */
export async function setAccessTokenCookie(token: string, options: SetAccessTokenOptions = {}) {
  const cookieStore = cookies();
  (await cookieStore).set({
    name: ACCESS_TOKEN_COOKIE_NAME,
    value: token,
    httpOnly: true,
    secure: isProduction,
    sameSite: 'lax',
    path: '/',
    maxAge: options.maxAge ?? ACCESS_TOKEN_MAX_AGE,
  });
}

/**
 * 저장된 액세스 토큰 쿠키를 제거.
 */
export async function clearAccessTokenCookie() {
  (await cookies()).delete(ACCESS_TOKEN_COOKIE_NAME);
}
