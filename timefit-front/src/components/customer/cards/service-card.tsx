import { Clock } from 'lucide-react';

import { cn } from '@/lib/utils';

interface ServiceCardProps {
  id: string;
  name: string;
  description: string;
  duration: number;
  price: number;
  image?: string;
  isSelected?: boolean;
  onToggle?: (id: string) => void;
  className?: string;
}

export function ServiceCard({
  id,
  name,
  description,
  duration,
  price,
  isSelected = false,
  onToggle,
  className,
}: ServiceCardProps) {
  const formatPrice = (price: number) => {
    return `${price.toLocaleString()}원`;
  };

  return (
    <div
      onClick={() => onToggle?.(id)}
      className={cn(
        'flex cursor-pointer gap-4 rounded-2xl border p-3 transition-all',
        isSelected ? 'border-[#3ec0c7] bg-[#f0fafa]' : 'border-gray-100',
        className
      )}
    >
      {/* 이미지 */}
      <div className="h-20 w-20 flex-shrink-0 rounded-xl bg-gray-200" />

      {/* 정보 */}
      <div className="flex flex-1 flex-col justify-between py-0.5">
        <div>
          <h3 className="font-semibold text-gray-900">{name}</h3>
          <p className="mt-0.5 line-clamp-2 text-xs text-gray-500">
            {description}
          </p>
        </div>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-1 text-xs text-gray-500">
            <Clock className="h-3.5 w-3.5" />
            <span>{duration}분</span>
          </div>
          <span className="font-semibold text-[#3ec0c7]">
            {formatPrice(price)}
          </span>
        </div>
      </div>

      {/* 선택 표시 */}
      <div className="flex items-center">
        <div
          className={cn(
            'flex h-6 w-6 items-center justify-center rounded-full border-2 transition-colors',
            isSelected ? 'border-[#3ec0c7] bg-[#3ec0c7]' : 'border-gray-300'
          )}
        >
          {isSelected && (
            <svg
              className="h-3.5 w-3.5 text-white"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                clipRule="evenodd"
              />
            </svg>
          )}
        </div>
      </div>
    </div>
  );
}
