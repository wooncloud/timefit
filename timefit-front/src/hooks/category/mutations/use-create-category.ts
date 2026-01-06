import { useState } from 'react';
import { toast } from 'sonner';

import type {
  Category,
  CreateCategoryRequest,
} from '@/types/category/category';
import { categoryService } from '@/services/category/category-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useCreateCategory(businessId: string) {
  const [loading, setLoading] = useState(false);

  const createCategory = async (
    data: CreateCategoryRequest
  ): Promise<Category | null> => {
    try {
      setLoading(true);

      const result = await categoryService.createCategory(businessId, data);

      if (handleAuthError(result)) {
        return null;
      }

      if (!result.success || !result.data) {
        toast.error(result.message || '카테고리 생성에 실패했습니다.');
        return null;
      }

      toast.success('카테고리가 생성되었습니다.');
      return result.data;
    } catch (err) {
      console.error('카테고리 생성 실패:', err);
      toast.error('카테고리 생성 중 오류가 발생했습니다.');
      return null;
    } finally {
      setLoading(false);
    }
  };

  return {
    createCategory,
    loading,
  };
}
