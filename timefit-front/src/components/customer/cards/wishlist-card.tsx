import Link from 'next/link';
import Image from 'next/image';
import { Heart, Star } from 'lucide-react';

import { cn } from '@/lib/utils';

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
    <Link href={`/places/${id}`} className="block">
      <div
        className={cn(
          'flex gap-4 rounded-2xl border border-gray-100 bg-white p-3 shadow-sm transition-shadow hover:shadow-md',
          className
        )}
      >
        {/* 이미지 */}
        <div className="relative h-24 w-24 flex-shrink-0 overflow-hidden rounded-xl bg-gray-200">
          {image && (
            <Image
              src={image}
              alt={name}
              fill
              className="object-cover"
              sizes="96px"
            />
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
                  e.stopPropagation();
                  onRemove(id);
                }}
                className="flex h-8 w-8 items-center justify-center"
              >
                <Heart className="h-5 w-5 fill-[#3ec0c7] text-[#3ec0c7]" />
              </button>
            </div>
            <p className="mt-0.5 text-sm text-gray-500">{description}</p>
          </div>

          <div className="flex items-center gap-1">
            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
            <span className="text-sm font-medium text-gray-700">{rating}</span>
            <span className="text-sm text-gray-400">({reviewCount})</span>
          </div>
        </div>
      </div>
    </Link>
  );
}
