import Link from 'next/link';
import { Heart, MapPin, Star } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

interface PlaceCardProps {
  id: string;
  name: string;
  description: string;
  rating: number;
  distance?: string;
  reviewCount?: number;
  badge?: string | null;
  image?: string;
  variant?: 'default' | 'wishlist';
  showAction?: boolean;
  onRemove?: (id: string) => void;
  className?: string;
}

export function PlaceCard({
  id,
  name,
  description,
  rating,
  distance,
  reviewCount,
  badge,
  variant = 'default',
  showAction = true,
  onRemove,
  className,
}: PlaceCardProps) {
  const content = (
    <>
      {/* 이미지 */}
      <div className="relative h-24 w-24 flex-shrink-0 overflow-hidden rounded-xl bg-gray-200">
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
            {variant === 'wishlist' && onRemove ? (
              <button
                onClick={e => {
                  e.preventDefault();
                  onRemove(id);
                }}
                className="flex h-8 w-8 items-center justify-center"
              >
                <Heart className="h-5 w-5 fill-[#3ec0c7] text-[#3ec0c7]" />
              </button>
            ) : (
              <div className="flex items-center gap-1">
                <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                <span className="text-sm font-medium text-gray-700">
                  {rating}
                </span>
              </div>
            )}
          </div>
          <p className="mt-0.5 text-sm text-gray-500">{description}</p>
        </div>

        <div className="flex items-center justify-between">
          {variant === 'wishlist' && reviewCount !== undefined ? (
            <div className="flex items-center gap-1">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="text-sm font-medium text-gray-700">
                {rating}
              </span>
              <span className="text-sm text-gray-400">({reviewCount})</span>
            </div>
          ) : distance ? (
            <div className="flex items-center gap-1 text-sm text-[#3ec0c7]">
              <MapPin className="h-4 w-4" />
              <span>{distance}</span>
            </div>
          ) : null}

          {showAction && (
            <>
              {variant === 'wishlist' ? (
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
              ) : (
                <Button
                  size="sm"
                  variant="outline"
                  className="h-8 rounded-full border-gray-200 text-xs font-medium"
                >
                  보기
                </Button>
              )}
            </>
          )}
        </div>
      </div>
    </>
  );

  const baseClassName = cn(
    'flex gap-4 rounded-2xl border border-gray-100 bg-white p-3 shadow-sm',
    variant !== 'wishlist' && 'transition-shadow hover:shadow-md',
    className
  );

  if (variant === 'wishlist') {
    return <div className={baseClassName}>{content}</div>;
  }

  return (
    <Link href={`/places/${id}`} className={baseClassName}>
      {content}
    </Link>
  );
}
