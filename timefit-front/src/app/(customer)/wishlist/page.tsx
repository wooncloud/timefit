'use client';

import { useState } from 'react';
import Link from 'next/link';
import { SlidersHorizontal, Star, Heart } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface WishlistItem {
  id: string;
  name: string;
  category: string;
  rating: number;
  reviewCount: number;
  image?: string;
}

// 찜 목록 더미 데이터
const initialWishlist: WishlistItem[] = [
  {
    id: '1',
    name: '제니스 피트니스 스튜디오',
    category: '필라테스 & 요가',
    rating: 4.9,
    reviewCount: 120
  },
  {
    id: '2',
    name: '아쿠아 웰니스 센터',
    category: '수중 테라피',
    rating: 4.7,
    reviewCount: 85
  },
  {
    id: '3',
    name: '아이언 피스트 복싱',
    category: '복싱 & HIIT',
    rating: 4.8,
    reviewCount: 210
  }
];

export default function WishlistPage() {
  const [wishlist, setWishlist] = useState(initialWishlist);

  const handleRemove = (id: string) => {
    setWishlist(wishlist.filter((item) => item.id !== id));
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4 py-4">
        <h1 className="text-xl font-bold text-gray-900">찜 목록</h1>
        <button className="flex h-10 w-10 items-center justify-center rounded-lg border border-gray-200">
          <SlidersHorizontal className="h-5 w-5 text-gray-600" />
        </button>
      </div>

      {/* 찜 목록 */}
      <div className="flex-1 px-4">
        {wishlist.length > 0 ? (
          <div className="space-y-4">
            {wishlist.map((item) => (
              <div
                key={item.id}
                className="flex gap-4 rounded-2xl border border-gray-100 bg-white p-3 shadow-sm"
              >
                {/* 이미지 */}
                <div className="h-24 w-24 flex-shrink-0 rounded-xl bg-gray-200" />

                {/* 정보 */}
                <div className="flex flex-1 flex-col justify-between py-1">
                  <div>
                    <div className="flex items-start justify-between">
                      <h3 className="font-semibold text-gray-900">{item.name}</h3>
                      <button
                        onClick={() => handleRemove(item.id)}
                        className="flex h-8 w-8 items-center justify-center"
                      >
                        <Heart className="h-5 w-5 fill-[#3ec0c7] text-[#3ec0c7]" />
                      </button>
                    </div>
                    <p className="text-sm text-gray-500">{item.category}</p>
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-1">
                      <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                      <span className="text-sm font-medium text-gray-700">{item.rating}</span>
                      <span className="text-sm text-gray-400">({item.reviewCount})</span>
                    </div>
                    <Link href={`/places/${item.id}/reserve`}>
                      <Button
                        size="sm"
                        variant="outline"
                        className="h-8 rounded-full border-[#3ec0c7] px-4 text-xs font-medium text-[#3ec0c7] hover:bg-[#3ec0c7] hover:text-white"
                      >
                        예약
                      </Button>
                    </Link>
                  </div>
                </div>
              </div>
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
