'use client';

import { useEffect, useState } from 'react';
import { businessService } from '@/services/business/business-service.client';
import type {
    BusinessListResponse,
    BusinessSearchParams,
} from '@/types/business/business';

/**
 * 업체 검색 훅 (GET /api/business/search)
 */
export function useBusinessSearch(params: BusinessSearchParams = {}) {
    const [data, setData] = useState<BusinessListResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const refetch = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const response = await businessService.searchBusinesses(params);
            if (response.data) {
                setData(response.data);
            }
        } catch (err) {
            const errorMessage =
                err instanceof Error
                    ? err.message
                    : '업체 목록을 불러오는데 실패했습니다.';
            setError(errorMessage);
            console.error('업체 검색 오류:', err);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        refetch();
    }, [params.keyword, params.businessType, params.region, params.page]);

    return { data, isLoading, error, refetch };
}
