import { useState } from 'react';
import { toast } from 'sonner';

import { categoryService } from '@/services/category/category-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeleteCategory(businessId: string) {
    const [loading, setLoading] = useState(false);

    const deleteCategory = async (categoryId: string): Promise<boolean> => {
        try {
            setLoading(true);

            const result = await categoryService.deleteCategory(businessId, categoryId);

            if (handleAuthError(result)) {
                return false;
            }

            if (!result.success) {
                toast.error(result.message || '카테고리 삭제에 실패했습니다.');
                return false;
            }

            toast.success('카테고리가 삭제되었습니다.');
            return true;
        } catch (err) {
            console.error('카테고리 삭제 실패:', err);
            toast.error('카테고리 삭제 중 오류가 발생했습니다.');
            return false;
        } finally {
            setLoading(false);
        }
    };

    return {
        deleteCategory,
        loading,
    };
}
