import { useState } from 'react';
import { toast } from 'sonner';

import { wishlistService } from '@/services/wishlist/wishlist-service.client';
import { handleAuthError } from '@/lib/api/handle-auth-error';

export function useAddWishlist() {
  const [loading, setLoading] = useState(false);

  const addWishlist = async (businessId: string): Promise<boolean> => {
    try {
      setLoading(true);
      const result = await wishlistService.addWishlist(businessId);

      if (handleAuthError(result)) {
        return false;
      }

      if (!result.success) {
        toast.error(result.message || '찜 추가에 실패했습니다.');
        return false;
      }

      // 백엔드 메시지가 있으면 사용, 없으면 기본 메시지
      const message = result.message || '찜 목록에 추가되었습니다.';

      // "이미 찜한" 메시지는 info로, 그 외에는 success로 표시
      if (message.includes('이미 찜')) {
        toast.info(message);
      } else {
        toast.success(message);
      }

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
