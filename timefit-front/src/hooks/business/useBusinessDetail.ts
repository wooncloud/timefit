'use client';

import { useState, useEffect } from 'react';
import type {
  PublicBusinessDetail,
  UpdateBusinessRequest,
} from '@/types/business/businessDetail';
import { handleAuthError } from '@/lib/api/handle-auth-error';

interface UseBusinessDetailResult {
  business: PublicBusinessDetail | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  updateBusiness: (data: UpdateBusinessRequest) => Promise<boolean>;
  updating: boolean;
}

/**
 * 업체 상세 정보를 조회하고 수정하는 Hook
 *
 * @param businessId - 조회할 업체 ID (UUID)
 * @returns 업체 상세 정보, 로딩 상태, 에러, 재조회 함수, 수정 함수
 *
 * @example
 * const { business, loading, error, refetch, updateBusiness, updating } = useBusinessDetail('uuid-here');
 */
export function useBusinessDetail(businessId: string): UseBusinessDetailResult {
  const [business, setBusiness] = useState<PublicBusinessDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [updating, setUpdating] = useState(false);

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

      // 인증 에러 체크 및 자동 리다이렉트
      if (handleAuthError(result)) {
        return;
      }

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

  const updateBusiness = async (
    data: UpdateBusinessRequest
  ): Promise<boolean> => {
    if (!businessId) {
      setError('업체 ID가 필요합니다.');
      return false;
    }

    setUpdating(true);
    setError(null);

    try {
      const response = await fetch(`/api/business/${businessId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      const result = await response.json();

      // 인증 에러 체크 및 자동 리다이렉트
      if (handleAuthError(result)) {
        return false;
      }

      if (!response.ok) {
        setError(result.message || '업체 정보 수정에 실패했습니다.');
        return false;
      }

      if (result.success) {
        // 수정 성공 후 데이터 새로고침
        await fetchBusinessDetail();
        return true;
      } else {
        setError('업체 정보 수정에 실패했습니다.');
        return false;
      }
    } catch (err) {
      console.error('업체 정보 수정 오류:', err);
      setError('서버 오류가 발생했습니다.');
      return false;
    } finally {
      setUpdating(false);
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
    updateBusiness,
    updating,
  };
}
