'use client';

import { useState, useEffect, useCallback } from 'react';
import { handleAuthError } from '@/lib/api/handle-auth-error';
import type {
  Menu,
  CreateUpdateMenuRequest,
  GetMenuDetailHandlerResponse,
  UpdateMenuHandlerResponse,
  DeleteMenuHandlerResponse,
} from '@/types/menu/menu';
import { useBusinessStore } from '@/store/business-store';

interface UseMenuDetailReturn {
  menu: Menu | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  updateMenu: (data: CreateUpdateMenuRequest) => Promise<boolean>;
  deleteMenu: () => Promise<boolean>;
  toggleMenu: () => Promise<boolean>;
  updating: boolean;
  deleting: boolean;
}

export function useMenuDetail(menuId: string | null): UseMenuDetailReturn {
  const { business } = useBusinessStore();
  const businessId = business?.businessId;

  const [menu, setMenu] = useState<Menu | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMenu = useCallback(async () => {
    if (!businessId || !menuId) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/menu/${menuId}`);
      const result: GetMenuDetailHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return;
      }

      if (!result.success || !result.data) {
        setError(result.message || '메뉴를 불러오는데 실패했습니다.');
        setMenu(null);
        return;
      }

      setMenu(result.data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '메뉴를 불러오는 중 오류가 발생했습니다.';
      console.error('Menu detail fetch error:', errorMessage);
      setError(errorMessage);
      setMenu(null);
    } finally {
      setLoading(false);
    }
  }, [businessId, menuId]);

  const updateMenu = async (data: CreateUpdateMenuRequest): Promise<boolean> => {
    if (!businessId || !menuId) {
      setError('비즈니스 정보 또는 메뉴 ID가 없습니다.');
      return false;
    }

    try {
      setUpdating(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/menu/${menuId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      const result: UpdateMenuHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success || !result.data) {
        setError(result.message || '메뉴 수정에 실패했습니다.');
        return false;
      }

      setMenu(result.data);
      return true;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '메뉴 수정 중 오류가 발생했습니다.';
      console.error('Menu update error:', errorMessage);
      setError(errorMessage);
      return false;
    } finally {
      setUpdating(false);
    }
  };

  const deleteMenu = async (): Promise<boolean> => {
    if (!businessId || !menuId) {
      setError('비즈니스 정보 또는 메뉴 ID가 없습니다.');
      return false;
    }

    try {
      setDeleting(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/menu/${menuId}`, {
        method: 'DELETE',
      });

      const result: DeleteMenuHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        setError(result.message || '메뉴 삭제에 실패했습니다.');
        return false;
      }

      setMenu(null);
      return true;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '메뉴 삭제 중 오류가 발생했습니다.';
      console.error('Menu delete error:', errorMessage);
      setError(errorMessage);
      return false;
    } finally {
      setDeleting(false);
    }
  };

  const toggleMenu = async (): Promise<boolean> => {
    if (!businessId || !menuId) {
      setError('비즈니스 정보 또는 메뉴 ID가 없습니다.');
      return false;
    }

    try {
      setUpdating(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/menu/${menuId}/toggle`, {
        method: 'PATCH',
      });

      const result: UpdateMenuHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success || !result.data) {
        setError(result.message || '메뉴 상태 변경에 실패했습니다.');
        return false;
      }

      setMenu(result.data);
      return true;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '메뉴 상태 변경 중 오류가 발생했습니다.';
      console.error('Menu toggle error:', errorMessage);
      setError(errorMessage);
      return false;
    } finally {
      setUpdating(false);
    }
  };

  useEffect(() => {
    fetchMenu();
  }, [fetchMenu]);

  return {
    menu,
    loading,
    error,
    refetch: fetchMenu,
    updateMenu,
    deleteMenu,
    toggleMenu,
    updating,
    deleting,
  };
}
