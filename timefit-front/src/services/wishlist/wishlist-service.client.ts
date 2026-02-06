import type {
  AddWishlistHandlerResponse,
  DeleteWishlistHandlerResponse,
} from '@/types/customer/wishlist';

/**
 * 클라이언트 측 클래스: 찜 관리 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class WishlistService {
  /**
   * 찜 추가 (API 라우트를 통한 클라이언트 측 호출)
   */
  async addWishlist(menuId: string): Promise<AddWishlistHandlerResponse> {
    const response = await fetch('/api/customer/wishlist', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ menuId }),
    });

    return response.json();
  }

  /**
   * 찜 삭제 (API 라우트를 통한 클라이언트 측 호출)
   */
  async deleteWishlist(menuId: string): Promise<DeleteWishlistHandlerResponse> {
    const response = await fetch(`/api/customer/wishlist/${menuId}`, {
      method: 'DELETE',
    });

    return response.json();
  }
}

export const wishlistService = new WishlistService();
