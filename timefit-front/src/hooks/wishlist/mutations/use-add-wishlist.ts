import { useState } from 'react';
import { toast } from 'sonner';

import { wishlistService } from '@/services/wishlist/wishlist-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useAddWishlist() {
  const [loading, setLoading] = useState(false);

  const addWishlist = async (menuId: string): Promise<boolean> => {
    try {
      setLoading(true);
      const result = await wishlistService.addWishlist(menuId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '찜 추가에 실패했습니다.');
        return false;
      }

      toast.success('찜 목록에 추가되었습니다.');
      return true;
    } catch (err) {
      console.error('찜 추가 실패:', err);
      toast.error('찜 추가 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { addWishlist, loading };
}
