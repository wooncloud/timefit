import Link from 'next/link';
import { Heart, Star } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

interface WishlistCardProps {
  id: string;
  name: string;
  description: string;
  rating: number;
  reviewCount: number;
  image?: string;
  onRemove: (id: string) => void;
  className?: string;
}

/**
 * 찜 목록용 카드
 */
export function WishlistCard({
  id,
  name,
  description,
  rating,
  reviewCount,
  image,
  onRemove,
  className,
}: WishlistCardProps) {
  return (
    <div
      className={cn(
        'flex gap-4 rounded-2xl border border-gray-100 bg-white p-3 shadow-sm',
        className
      )}
    >
      {/* 이미지 */}
      <div className="relative h-24 w-24 flex-shrink-0 overflow-hidden rounded-xl bg-gray-200">
        {image && (
          <img src={image} alt={name} className="h-full w-full object-cover" />
        )}
      </div>

      {/* 정보 */}
      <div className="flex flex-1 flex-col justify-between py-1">
        <div>
          <div className="flex items-start justify-between">
            <h3 className="font-semibold text-gray-900">{name}</h3>
            <button
              onClick={e => {
                e.preventDefault();
                onRemove(id);
              }}
              className="flex h-8 w-8 items-center justify-center"
            >
              <Heart className="h-5 w-5 fill-[#3ec0c7] text-[#3ec0c7]" />
            </button>
          </div>
          <p className="mt-0.5 text-sm text-gray-500">{description}</p>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1">
            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
            <span className="text-sm font-medium text-gray-700">{rating}</span>
            <span className="text-sm text-gray-400">({reviewCount})</span>
          </div>

          <Link
            href={`/places/${id}/reserve`}
            onClick={e => e.stopPropagation()}
          >
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
  );
}
