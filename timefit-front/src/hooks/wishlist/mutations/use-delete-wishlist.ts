import { useState } from 'react';
import { toast } from 'sonner';

import { wishlistService } from '@/services/wishlist/wishlist-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useDeleteWishlist() {
  const [loading, setLoading] = useState(false);

  const deleteWishlist = async (menuId: string): Promise<boolean> => {
    try {
      setLoading(true);
      const result = await wishlistService.deleteWishlist(menuId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '찜 삭제에 실패했습니다.');
        return false;
      }

      toast.success('찜 목록에서 제거되었습니다.');
      return true;
    } catch (err) {
      console.error('찜 삭제 실패:', err);
      toast.error('찜 삭제 중 오류가 발생했습니다.');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return { deleteWishlist, loading };
}
