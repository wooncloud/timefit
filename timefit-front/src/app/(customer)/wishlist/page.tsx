import { getWishlistList } from '@/services/wishlist/wishlist-service';

import { WishlistClient } from './wishlist-client';

export default async function WishlistPage() {
  // SSR: 서버에서 데이터 조회
  const wishlistData = await getWishlistList(0, 20);

  return <WishlistClient initialData={wishlistData} />;
}
