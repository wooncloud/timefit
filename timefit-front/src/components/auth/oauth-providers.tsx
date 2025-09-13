'use client';

import { createClient } from '@/lib/supabase/client';
import { useState } from 'react';

export function OAuthProviders() {
  const [loading, setLoading] = useState<string | null>(null);
  const supabase = createClient();

  const handleOAuth = async (provider: 'google' | 'kakao') => {
    setLoading(provider);

    try {
      const { error } = await supabase.auth.signInWithOAuth({
        provider,
        options: {
          redirectTo: `${window.location.origin}/auth/callback`,
        },
      });

      if (error) {
        console.error('OAuth error:', error);
      }
    } catch (error) {
      console.error('OAuth error:', error);
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="space-y-3">
      <button
        onClick={() => handleOAuth('google')}
        disabled={loading === 'google'}
        className="flex w-full items-center justify-center rounded-lg border border-input px-4 py-3 transition-colors hover:bg-accent disabled:opacity-50"
      >
        <svg className="mr-3 h-5 w-5" viewBox="0 0 24 24">
          <path
            fill="#4285F4"
            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
          />
          <path
            fill="#34A853"
            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
          />
          <path
            fill="#FBBC05"
            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
          />
          <path
            fill="#EA4335"
            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
          />
        </svg>
        {loading === 'google' ? '로그인 중...' : 'Google로 로그인'}
      </button>

      <button
        onClick={() => handleOAuth('kakao')}
        disabled={loading === 'kakao'}
        className="flex w-full items-center justify-center rounded-lg bg-yellow-400 px-4 py-3 text-black transition-colors hover:bg-yellow-500 disabled:opacity-50"
      >
        <svg className="mr-3 h-5 w-5" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 3c5.799 0 10.5 3.664 10.5 8.185 0 4.52-4.701 8.184-10.5 8.184a13.5 13.5 0 0 1-1.727-.11l-4.408 2.883c-.501.265-.678.236-.472-.413l.892-3.678c-2.88-1.46-4.785-3.99-4.785-6.866C1.5 6.665 6.201 3 12 3z" />
        </svg>
        {loading === 'kakao' ? '로그인 중...' : '카카오로 로그인'}
      </button>
    </div>
  );
}
