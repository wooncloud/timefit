'use client';

import { useCallback, useEffect, useState } from 'react';

import type {
  Category,
  CreateCategoryHandlerResponse,
  CreateCategoryRequest,
  DeleteCategoryHandlerResponse,
  GetCategoryListHandlerResponse,
  UpdateCategoryHandlerResponse,
  UpdateCategoryRequest,
} from '@/types/category/category';
import { useBusinessStore } from '@/store/business-store';
import { handleAuthError } from '@/lib/api/handle-auth-error';

interface UseCategoryListReturn {
  categories: Category[];
  totalCount: number;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
  createCategory: (data: CreateCategoryRequest) => Promise<Category | null>;
  updateCategory: (id: string, data: UpdateCategoryRequest) => Promise<boolean>;
  deleteCategory: (id: string) => Promise<boolean>;
  creating: boolean;
  updating: boolean;
  deleting: boolean;
}

export function useCategoryList(): UseCategoryListReturn {
  const { business } = useBusinessStore();
  const businessId = business?.businessId;

  const [categories, setCategories] = useState<Category[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchCategories = useCallback(async () => {
    if (!businessId) {
      setError('비즈니스 정보가 없습니다.');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/category`);
      const result: GetCategoryListHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return;
      }

      if (!result.success || !result.data) {
        setError(result.message || '카테고리 목록을 불러오는데 실패했습니다.');
        setCategories([]);
        setTotalCount(0);
        return;
      }

      setCategories(result.data.categories);
      setTotalCount(result.data.totalCount);
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '카테고리 목록을 불러오는 중 오류가 발생했습니다.';
      console.error('Category list fetch error:', errorMessage);
      setError(errorMessage);
      setCategories([]);
      setTotalCount(0);
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  const createCategory = async (
    data: CreateCategoryRequest
  ): Promise<Category | null> => {
    if (!businessId) {
      setError('비즈니스 정보가 없습니다.');
      return null;
    }

    try {
      setCreating(true);
      setError(null);

      const response = await fetch(`/api/business/${businessId}/category`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      const result: CreateCategoryHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return null;
      }

      if (!result.success || !result.data) {
        setError(result.message || '카테고리 생성에 실패했습니다.');
        return null;
      }

      await fetchCategories();
      return result.data;
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '카테고리 생성 중 오류가 발생했습니다.';
      console.error('Category creation error:', errorMessage);
      setError(errorMessage);
      return null;
    } finally {
      setCreating(false);
    }
  };

  const updateCategory = async (
    id: string,
    data: UpdateCategoryRequest
  ): Promise<boolean> => {
    if (!businessId) {
      setError('비즈니스 정보가 없습니다.');
      return false;
    }

    try {
      setUpdating(true);
      setError(null);

      const response = await fetch(
        `/api/business/${businessId}/category/${id}`,
        {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data),
        }
      );

      const result: UpdateCategoryHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        setError(result.message || '카테고리 수정에 실패했습니다.');
        return false;
      }

      await fetchCategories();
      return true;
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '카테고리 수정 중 오류가 발생했습니다.';
      console.error('Category update error:', errorMessage);
      setError(errorMessage);
      return false;
    } finally {
      setUpdating(false);
    }
  };

  const deleteCategory = async (id: string): Promise<boolean> => {
    if (!businessId) {
      setError('비즈니스 정보가 없습니다.');
      return false;
    }

    try {
      setDeleting(true);
      setError(null);

      const response = await fetch(
        `/api/business/${businessId}/category/${id}`,
        {
          method: 'DELETE',
        }
      );

      const result: DeleteCategoryHandlerResponse = await response.json();

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        setError(result.message || '카테고리 삭제에 실패했습니다.');
        return false;
      }

      await fetchCategories();
      return true;
    } catch (err) {
      const errorMessage =
        err instanceof Error
          ? err.message
          : '카테고리 삭제 중 오류가 발생했습니다.';
      console.error('Category delete error:', errorMessage);
      setError(errorMessage);
      return false;
    } finally {
      setDeleting(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  return {
    categories,
    totalCount,
    loading,
    error,
    refetch: fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    creating,
    updating,
    deleting,
  };
}
