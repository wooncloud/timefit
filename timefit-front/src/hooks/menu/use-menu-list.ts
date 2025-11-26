'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';

import type {
  CreateMenuHandlerResponse,
  CreateUpdateMenuRequest,
  GetMenuListHandlerResponse,
  Menu,
} from '@/types/menu/menu';
import { useBusinessStore } from '@/store/business-store';
import { handleAuthError } from '@/lib/api/handle-auth-error';

interface UseMenuListOptions {
  serviceName?: string;
  businessCategoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  isActive?: boolean;
}

interface UseMenuListReturn {
  menus: Menu[];
  totalCount: number;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  createMenu: (data: CreateUpdateMenuRequest) => Promise<Menu | null>;
  creating: boolean;
}

export function useMenuList(
  options: UseMenuListOptions = {}
): UseMenuListReturn {
  const { business } = useBusinessStore();
  const businessId = business?.businessId;

  const [menus, setMenus] = useState<Menu[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const memoizedOptions = useMemo(
    () => options,
    [
      options.serviceName,
      options.businessCategoryId,
      options.minPrice,
      options.maxPrice,
      options.isActive,
    ]
  );

  const fetchMenus = useCallback(async () => {
    if (!businessId) {
      setError('비즈니스 정보가 없습니다.');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const params = new URLSearchParams();
      if (memoizedOptions.serviceName) {
        params.append('serviceName', memoizedOptions.serviceName);
      }
      if (memoizedOptions.businessCategoryId) {
        params.append('businessCategoryId', memoizedOptions.businessCategoryId);
      }
      if (memoizedOptions.minPrice !== undefined) {
        params.append('minPrice', memoizedOptions.minPrice.toString());
      }
      if (memoizedOptions.maxPrice !== undefined) {
        params.append('maxPrice', memoizedOptions.maxPrice.toString());
      }
      if (memoizedOptions.isActive !== undefined) {
        params.append('isActive', memoizedOptions.isActive.toString());
      }

      const queryString = params.toString();
      const url = `/api/business/${businessId}/menu${queryString ? `?${queryString}` : ''}`;

      const response = await fetch(url);
      const result: GetMenuListHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return;
      }

      if (!result.success || !result.data) {
        setError(result.message || '메뉴 목록을 불러오는데 실패했습니다.');
        setMenus([]);
        setTotalCount(0);
        return;
      }

      setMenus(result.data.menus);
      setTotalCount(result.data.totalCount);
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '메뉴 목록을 불러오는 중 오류가 발생했습니다.';
      console.error('Menu list fetch error:', errorMessage);
      setError(errorMessage);
      setMenus([]);
      setTotalCount(0);
    } finally {
      setLoading(false);
    }
  }, [businessId, memoizedOptions]);

  const createMenu = async (
    data: CreateUpdateMenuRequest
  ): Promise<Menu | null> => {
    if (!businessId) {
      setError('비즈니스 정보가 없습니다.');
      return null;
    }

    try {
      setCreating(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/menu`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      const result: CreateMenuHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return null;
      }

      if (!result.success || !result.data) {
        setError(result.message || '메뉴 생성에 실패했습니다.');
        return null;
      }

      return result.data;
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '메뉴 생성 중 오류가 발생했습니다.';
      console.error('Menu creation error:', errorMessage);
      setError(errorMessage);
      return null;
    } finally {
      setCreating(false);
    }
  };

  useEffect(() => {
    fetchMenus();
  }, [fetchMenus]);

  return {
    menus,
    totalCount,
    loading,
    error,
    refetch: fetchMenus,
    createMenu,
    creating,
  };
}
