'use client';

import { useState, useEffect } from 'react';
import type { PublicBusinessDetail } from '@/types/business/businessDetail';

interface UseBusinessDetailResult {
  business: PublicBusinessDetail | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
}

/**
 * 업체 상세 정보를 조회하는 Hook
 *
 * @param businessId - 조회할 업체 ID (UUID)
 * @returns 업체 상세 정보, 로딩 상태, 에러, 재조회 함수
 *
 * @example
 * const { business, loading, error, refetch } = useBusinessDetail('uuid-here');
 */
export function useBusinessDetail(businessId: string): UseBusinessDetailResult {
  const [business, setBusiness] = useState<PublicBusinessDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchBusinessDetail = async () => {
    if (!businessId) {
      setError('업체 ID가 필요합니다.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`/api/business/${businessId}`);
      const result = await response.json();

      if (!response.ok) {
        setError(result.message || '업체 정보 조회에 실패했습니다.');
        setBusiness(null);
        return;
      }

      if (result.success && result.data) {
        setBusiness(result.data);
      } else {
        setError('업체 정보를 찾을 수 없습니다.');
        setBusiness(null);
      }
    } catch (err) {
      console.error('업체 상세 조회 오류:', err);
      setError('서버 오류가 발생했습니다.');
      setBusiness(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBusinessDetail();
  }, [businessId]);

  return {
    business,
    loading,
    error,
    refetch: fetchBusinessDetail,
  };
}
