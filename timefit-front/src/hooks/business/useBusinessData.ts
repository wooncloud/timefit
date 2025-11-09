'use client';

import { useEffect } from 'react';
import { useBusinessStore } from '@/store';
import { businessService } from '@/services/business/businessService';

/**
 * Layout에서 사용하는 hook
 * 비즈니스 섹션 진입 시 데이터 로드
 *
 * SessionStoreProvider가 세션에서 business 데이터를 이미 저장하므로
 * 이 hook은 세션에 business 데이터가 없는 경우에만 API를 호출합니다.
 */
export function useBusinessData() {
  const { business, _hasHydrated, setBusiness, setLoading } =
    useBusinessStore();

  useEffect(() => {
    // Hydration이 완료될 때까지 대기
    if (!_hasHydrated) return;

    const fetchData = async () => {
      // Store에 이미 데이터가 있으면 스킵 (SessionStoreProvider가 세션에서 저장함)
      if (business) return;

      // 세션에도 없는 경우에만 API 호출
      setLoading(true);
      try {
        const result = await businessService.getMyBusiness();
        if (result.success && result.data) {
          setBusiness(result.data[0] || null);
        }
      } catch (error) {
        console.error('Failed to fetch businesses:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [_hasHydrated, setBusiness, setLoading]);
}
