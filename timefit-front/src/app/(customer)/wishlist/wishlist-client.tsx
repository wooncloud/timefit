'use client';

import Link from 'next/link';
import { Heart } from 'lucide-react';

import type { WishlistList } from '@/types/customer/wishlist';
import { useDeleteWishlist } from '@/hooks/wishlist/mutations/use-delete-wishlist';
import { WishlistCard } from '@/components/customer/cards/wishlist-card';
import { Button } from '@/components/ui/button';

interface WishlistClientProps {
  initialData: WishlistList;
}

export function WishlistClient({ initialData }: WishlistClientProps) {
  const { deleteWishlist } = useDeleteWishlist();

  const handleRemove = async (menuId: string) => {
    const success = await deleteWishlist(menuId);
    if (success) {
      window.location.reload(); // SSR 데이터 재요청
    }
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4 py-4">
        <h1 className="text-xl font-bold text-gray-900">찜 목록</h1>
      </div>

      {/* 찜 목록 */}
      <div className="flex-1 px-4">
        {initialData.wishlists.length > 0 ? (
          <div className="space-y-4">
            {initialData.wishlists.map(item => (
              <WishlistCard
                key={item.wishlistId}
                id={item.menuId}
                name={item.menuName}
                description={item.businessName}
                rating={0}
                reviewCount={0}
                image={item.imageUrl}
                onRemove={() => handleRemove(item.menuId)}
              />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-20">
            <Heart className="h-12 w-12 text-gray-300" />
            <p className="mt-4 text-gray-500">찜한 장소가 없습니다</p>
            <Link href="/" className="mt-4">
              <Button
                variant="outline"
                className="rounded-xl border-[#3ec0c7] text-[#3ec0c7]"
              >
                장소 둘러보기
              </Button>
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
