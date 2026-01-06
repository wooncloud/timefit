import { useState } from 'react';
import { toast } from 'sonner';

import type { UpdateCategoryRequest } from '@/types/category/category';
import { categoryService } from '@/services/category/category-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useUpdateCategory(businessId: string) {
  const [loading, setLoading] = useState(false);

  const updateCategory = async (
    categoryId: string,
    data: UpdateCategoryRequest
  ): Promise<boolean> => {
    try {
      setLoading(true);

      const result = await categoryService.updateCategory(
        businessId,
        categoryId,
        data
      );

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '카테고리 수정에 실패했습니다.');
        return false;
      }

      toast.success('카테고리가 수정되었습니다.');
      return true;
    } catch (err) {
      console.error('카테고리 수정 실패:', err);
      toast.error('카테고리 수정 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    updateCategory,
    loading,
  };
}
