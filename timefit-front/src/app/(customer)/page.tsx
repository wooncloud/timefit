'use client';

import Link from 'next/link';
import {
  Coffee,
  Dumbbell,
  Heart,
  Search,
  Sparkles,
} from 'lucide-react';

import { BusinessCard } from '@/components/customer/cards/business-card';
import { Input } from '@/components/ui/input';
import { useBusinessSearch } from '@/hooks/business/use-business-search';
import { getBusinessTypeNames } from '@/lib/formatters/business-formatter';

// 카테고리 더미 데이터
const categories = [
  { id: 'beauty', name: '뷰티', icon: Sparkles, color: '#3ec0c7' },
  { id: 'sports', name: '스포츠', icon: Dumbbell, color: '#f97316' },
  { id: 'cafe', name: '카페', icon: Coffee, color: '#8b5cf6' },
  { id: 'health', name: '건강', icon: Heart, color: '#ec4899' },
];

export default function HomePage() {
  const { data: businessData, isLoading } = useBusinessSearch({
    page: 0,
    size: 20,
  });

  return (
    <div className="bg-white px-4 py-6">
      {/* 타이틀 */}
      <h1 className="mb-4 text-2xl font-bold text-gray-900">
        나만의 <span className="text-[#3ec0c7]">완벽한 시간</span>을 찾아보세요
      </h1>

      {/* 검색바 */}
      <div className="relative mb-6">
        <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" />
        <Input
          type="text"
          placeholder="요가, 마사지 등을 검색해보세요..."
          className="h-12 rounded-xl border-gray-200 pl-10 pr-12"
        />
      </div>

      {/* 카테고리 */}
      <div className="mb-6">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">카테고리</h2>
        </div>
        <div className="flex gap-4 overflow-x-auto pb-2">
          {categories.map(category => {
            const Icon = category.icon;
            return (
              <Link
                key={category.id}
                href={`/search?category=${category.id}`}
                className="flex flex-col items-center gap-2"
              >
                <div
                  className="flex h-16 w-16 items-center justify-center rounded-2xl"
                  style={{ backgroundColor: `${category.color}15` }}
                >
                  <Icon className="h-7 w-7" style={{ color: category.color }} />
                </div>
                <span className="text-sm font-medium text-gray-600">
                  {category.name}
                </span>
              </Link>
            );
          })}
        </div>
      </div>

      {/* 장소 리스트 */}
      <div>
        <h2 className="mb-4 text-lg font-semibold text-gray-900">추천 장소</h2>

        {isLoading ? (
          <div className="flex min-h-[200px] items-center justify-center">
            <div className="text-gray-500">로딩 중...</div>
          </div>
        ) : !businessData || businessData.businesses.length === 0 ? (
          <div className="flex min-h-[200px] items-center justify-center">
            <div className="text-gray-500">등록된 업체가 없습니다.</div>
          </div>
        ) : (
          <div className="space-y-4">
            {businessData.businesses.map(business => (
              <BusinessCard
                key={business.businessId}
                id={business.businessId}
                name={business.businessName}
                description={getBusinessTypeNames(business.businessTypes).join(', ')}
                rating={4.5}
                badge={null}
                image={business.logoUrl || '/images/placeholder.jpg'}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
