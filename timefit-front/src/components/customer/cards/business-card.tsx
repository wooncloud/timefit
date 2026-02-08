import Link from 'next/link';
import Image from 'next/image';
import { Star } from 'lucide-react';

import { cn } from '@/lib/utils';

interface BusinessCardProps {
  id: string;
  name: string;
  description: string;
  rating: number;
  badge?: string | null;
  image?: string;
  className?: string;
}

/**
 * 업체 카드 (기본 목록용)
 */
export function BusinessCard({
  id,
  name,
  description,
  rating,
  badge,
  image,
  className,
}: BusinessCardProps) {
  return (
    <Link
      href={`/places/${id}`}
      className={cn(
        'flex gap-4 rounded-2xl border border-gray-100 bg-white p-3 shadow-sm transition-shadow hover:shadow-md',
        className
      )}
    >
      {/* 이미지 */}
      <div className="relative h-24 w-24 flex-shrink-0 overflow-hidden rounded-xl bg-gray-200">
        {image && (
          <Image src={image} alt={name} fill className="object-cover" sizes="96px" />
        )}
        {badge && (
          <span
            className={cn(
              'absolute left-2 top-2 rounded-full px-2 py-0.5 text-xs font-semibold text-white',
              badge === '신규' ? 'bg-[#3ec0c7]' : 'bg-orange-500'
            )}
          >
            {badge}
          </span>
        )}
      </div>

      {/* 정보 */}
      <div className="flex flex-1 flex-col justify-between py-1">
        <div>
          <div className="flex items-start justify-between">
            <h3 className="font-semibold text-gray-900">{name}</h3>
            <div className="flex items-center gap-1">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="text-sm font-medium text-gray-700">
                {rating}
              </span>
            </div>
          </div>
          <p className="mt-0.5 text-sm text-gray-500">{description}</p>
        </div>
      </div>
    </Link>
  );
}
