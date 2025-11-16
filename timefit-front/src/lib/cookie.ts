import { cookies } from 'next/headers';

export const ACCESS_TOKEN_COOKIE_NAME = 'accessToken';
export const ACCESS_TOKEN_MAX_AGE = 60 * 15; // 15 minutes

export const REFRESH_TOKEN_COOKIE_NAME = 'refreshToken';
export const REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 7; // 7 days

const isProduction = process.env.NODE_ENV === 'production';

interface SetAccessTokenOptions {
  maxAge?: number;
}

/**
 * 액세스 토큰을 공용 쿠키로 저장.
 */
export async function setAccessTokenCookie(
  token: string,
  options: SetAccessTokenOptions = {}
) {
  (await cookies()).set({
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

/**
 * 쿠키에 저장된 액세스 토큰을 반환.
 */
export async function getAccessTokenFromCookie(): Promise<string | undefined> {
  return (await cookies()).get(ACCESS_TOKEN_COOKIE_NAME)?.value;
}

/**
 * 액세스 토큰 쿠키 존재 여부를 확인.
 */
export async function hasAccessTokenCookie(): Promise<boolean> {
  return Boolean(await getAccessTokenFromCookie());
}

interface SetRefreshTokenOptions {
  maxAge?: number;
}

/**
 * 리프레시 토큰을 httpOnly 쿠키로 저장.
 */
export async function setRefreshTokenCookie(
  token: string,
  options: SetRefreshTokenOptions = {}
) {
  (await cookies()).set({
    name: REFRESH_TOKEN_COOKIE_NAME,
    value: token,
    httpOnly: true,
    secure: isProduction,
    sameSite: 'lax',
    path: '/',
    maxAge: options.maxAge ?? REFRESH_TOKEN_MAX_AGE,
  });
}

/**
 * 저장된 리프레시 토큰 쿠키를 제거.
 */
export async function clearRefreshTokenCookie() {
  (await cookies()).delete(REFRESH_TOKEN_COOKIE_NAME);
}

/**
 * 쿠키에 저장된 리프레시 토큰을 반환.
 */
export async function getRefreshTokenFromCookie(): Promise<string | undefined> {
  return (await cookies()).get(REFRESH_TOKEN_COOKIE_NAME)?.value;
}

/**
 * 리프레시 토큰 쿠키 존재 여부를 확인.
 */
export async function hasRefreshTokenCookie(): Promise<boolean> {
  return Boolean(await getRefreshTokenFromCookie());
}
