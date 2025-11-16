import { SessionOptions } from 'iron-session';

import type { AuthUserProfile } from '@/types/auth/user';

export type SessionUser = AuthUserProfile & {
  accessToken?: string;
  refreshToken?: string;
};
export type SessionData = {
  user?: SessionUser | null;
};

export const sessionOptions: SessionOptions = {
  cookieName: 'timefit_session',
  password: process.env.IRON_SESSION_PASSWORD!,
  cookieOptions: {
    secure: process.env.NODE_ENV === 'production',
    httpOnly: true,
    sameSite: 'lax',
  },
};
