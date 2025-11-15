'use client';

import { useEffect } from 'react';
import { useUserStore } from '@/store/user-store';
import { useBusinessStore } from '@/store/business-store';
import { useBusinessData } from '@/hooks/business/useBusinessData';
import type { SessionUser } from '@/lib/session/options';

interface BusinessLayoutProviderProps {
  children: React.ReactNode;
  sessionUser: SessionUser | null;
}

/**
 * Business Layout Provider
 * - 세션 데이터를 Zustand store에 동기화
 * - 토큰은 제외하고 사용자/비즈니스 정보만 저장
 * - 비즈니스 데이터 로드 관리
 */
export function BusinessLayoutProvider({
  children,
  sessionUser,
}: BusinessLayoutProviderProps) {
  const setUser = useUserStore((state) => state.setUser);
  const setBusiness = useBusinessStore((state) => state.setBusiness);

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

  // 세션에 없는 경우 API에서 비즈니스 데이터 로드
  useBusinessData();

  return <>{children}</>;
}
