import 'server-only';

import { cookies } from 'next/headers';
import { getIronSession } from 'iron-session';

import { sessionOptions, SessionData, SessionUser } from '@/lib/session/options';

export async function getServerSession() {
  return getIronSession<SessionData>(await cookies(), sessionOptions);
}

export async function getCurrentUserFromSession(): Promise<SessionUser | null> {
  const session = await getServerSession();
  return session.user ?? null;
}
