import { SessionOptions } from 'iron-session';

import type { AuthUserProfile } from '@/types/auth/user';

export type SessionUser = AuthUserProfile & {
  accessToken?: string;
  refreshToken?: string;
};
export type SessionData = {
  user?: SessionUser | null;
};

// 환경변수 검증
const password = process.env.IRON_SESSION_PASSWORD;
if (!password) {
  throw new Error(
    '[Session] IRON_SESSION_PASSWORD 환경변수가 설정되지 않았습니다.'
  );
}
if (password.length < 32) {
  throw new Error(
    '[Session] IRON_SESSION_PASSWORD는 최소 32자 이상이어야 합니다.'
  );
}

// 세션 TTL: 7일 (초 단위)
const SESSION_TTL = 60 * 60 * 24 * 7;

export const sessionOptions: SessionOptions = {
  cookieName: 'timefit_session',
  password,
  ttl: SESSION_TTL,
  cookieOptions: {
    secure: process.env.NODE_ENV === 'production',
    httpOnly: true,
    sameSite: 'lax',
    maxAge: SESSION_TTL, // 쿠키 만료도 동기화
  },
};
