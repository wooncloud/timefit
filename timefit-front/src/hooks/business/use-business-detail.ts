'use client';

import { useEffect, useState } from 'react';

import type { PublicBusinessDetail } from '@/types/business/business';
import { businessService } from '@/services/business/business-service.client';

/**
 * 업체 상세 조회 훅 (GET /api/business/:businessId)
 */
export function useBusinessDetail(businessId: string | null) {
  const [data, setData] = useState<PublicBusinessDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refetch = async () => {
    if (!businessId) {
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      const response = await businessService.getBusinessDetail(businessId);
      if (response.data) {
        setData(response.data);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '업체 정보를 불러오는데 실패했습니다.';
      setError(errorMessage);
      console.error('업체 상세 조회 오류:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    refetch();
  }, [businessId]);

  return { data, isLoading, error, refetch };
}
