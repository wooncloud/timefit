'use client';

import { useEffect } from 'react';

import { useUserActions } from '@/store';
import { useBusinessActions } from '@/store';
import type { SessionUser } from '@/lib/session/options';

interface BusinessLayoutProviderProps {
  children: React.ReactNode;
  sessionUser: SessionUser | null;
}

/**
 * 비즈니스 레이아웃 프로바이더
 * - 세션 데이터를 Zustand store에 동기화
 * - 토큰은 제외하고 사용자/비즈니스 정보만 저장
 * - 비즈니스 데이터 로드 관리
 */
export function BusinessLayoutProvider({
  children,
  sessionUser,
}: BusinessLayoutProviderProps) {
  const { setUser } = useUserActions();
  const { setBusiness } = useBusinessActions();

  // 세션 데이터를 store에 동기화
  useEffect(() => {
    if (!sessionUser) {
      setUser(null);
      setBusiness(null);
      return;
    }

    // 사용자 정보 저장 (토큰과 비즈니스 정보 제외)
    const { businesses, ...userInfo } = sessionUser;
    setUser({
      userId: userInfo.userId,
      email: userInfo.email,
      name: userInfo.name,
      phoneNumber: userInfo.phoneNumber,
      role: userInfo.role,
      profileImageUrl: userInfo.profileImageUrl,
      lastLoginAt: userInfo.lastLoginAt,
      createdAt: userInfo.createdAt,
    });

    // 첫 번째 business를 store에 저장
    if (businesses && businesses.length > 0) {
      const firstBusiness = businesses[0];
      setBusiness(firstBusiness);
    }
  }, [sessionUser, setUser, setBusiness]);

  return <>{children}</>;
}
