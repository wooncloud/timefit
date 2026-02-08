'use client';

import { useState } from 'react';

import { useAddWishlist } from './mutations/use-add-wishlist';
import { useDeleteWishlist } from './mutations/use-delete-wishlist';

/**
 * 찜 상태를 관리하는 통합 훅
 * Heart 버튼에서 사용
 */
export function useWishlistStatus(
  businessId: string,
  initialStatus: boolean = false
) {
  const [isWishlisted, setIsWishlisted] = useState(initialStatus);
  const { addWishlist, loading: addLoading } = useAddWishlist();
  const { deleteWishlist, loading: deleteLoading } = useDeleteWishlist();

  const toggleWishlist = async () => {
    if (isWishlisted) {
      const success = await deleteWishlist(businessId);
      if (success) {
        setIsWishlisted(false);
      }
    } else {
      const success = await addWishlist(businessId);
      if (success) {
        setIsWishlisted(true);
      }
    }
  };

  return {
    isWishlisted,
    loading: addLoading || deleteLoading,
    toggleWishlist,
  };
}
