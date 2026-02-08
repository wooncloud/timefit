'use client';

import { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import {
  ChevronLeft,
  Heart,
  Info,
  Megaphone,
  Share2,
  Star,
} from 'lucide-react';

import type { PublicBusinessDetail } from '@/types/business/business';
import type { MenuList } from '@/types/customer/menu';
import { useWishlistStatus } from '@/hooks/wishlist/use-wishlist-status';
import { getBusinessTypeNames } from '@/lib/formatters/business-formatter';
import { cn } from '@/lib/utils';
import { ServiceCard } from '@/components/customer/cards/service-card';
import { Button } from '@/components/ui/button';

type TabType = 'info' | 'services' | 'reviews';

interface PlaceDetailClientProps {
  business: PublicBusinessDetail;
  businessId: string;
  initialWishlistStatus: boolean;
  menuList: MenuList;
}

export function PlaceDetailClient({
  business,
  businessId,
  initialWishlistStatus,
  menuList,
}: PlaceDetailClientProps) {
  const [activeTab, setActiveTab] = useState<TabType>('services');
  const [selectedServices, setSelectedServices] = useState<string[]>([]);

  const { isWishlisted, loading, toggleWishlist } = useWishlistStatus(
    businessId,
    initialWishlistStatus
  );

  const toggleService = (serviceId: string) => {
    setSelectedServices(prev =>
      prev.includes(serviceId)
        ? prev.filter(id => id !== serviceId)
        : [...prev, serviceId]
    );
  };

  const selectedCount = selectedServices.length;
  const totalPrice = menuList.menus
    .filter(menu => selectedServices.includes(menu.menuId))
    .reduce((sum, menu) => sum + menu.price, 0);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('ko-KR').format(price) + '원';
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 상단 이미지 영역 */}
      <div className="relative">
        {/* 메인 이미지 */}
        <div className="flex gap-1">
          <div className="relative h-56 flex-1 bg-gray-300">
            {business.logoUrl && (
              <Image
                src={business.logoUrl}
                alt={business.businessName}
                fill
                className="object-cover"
                sizes="(max-width: 768px) 100vw, 50vw"
              />
            )}
          </div>
          <div className="flex w-20 flex-col gap-1">
            <div className="h-[calc(50%-2px)] bg-gray-200" />
            <div className="h-[calc(50%-2px)] bg-gray-200" />
          </div>
        </div>

        {/* 상단 네비게이션 */}
        <div className="absolute left-0 right-0 top-0 flex items-center justify-between p-4">
          <Link
            href="/"
            className="flex h-10 w-10 items-center justify-center rounded-full bg-white/80 backdrop-blur-sm"
          >
            <ChevronLeft className="h-6 w-6 text-gray-700" />
          </Link>
          <div className="flex gap-2">
            <button
              onClick={toggleWishlist}
              disabled={loading}
              className="flex h-10 w-10 items-center justify-center rounded-full bg-white/80 backdrop-blur-sm disabled:opacity-50"
            >
              <Heart
                className={cn(
                  'h-6 w-6 transition-colors',
                  isWishlisted ? 'fill-red-500 text-red-500' : 'text-gray-700'
                )}
              />
            </button>
            <button className="flex h-10 w-10 items-center justify-center rounded-full bg-white/80 backdrop-blur-sm">
              <Share2 className="h-5 w-5 text-gray-700" />
            </button>
          </div>
        </div>
      </div>

      {/* 업체 정보 */}
      <div className="px-4 py-4">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-xl font-bold text-gray-900">
              {business.businessName}
            </h1>
            <div className="mt-1 flex items-center gap-2 text-sm text-gray-500">
              <span>
                {getBusinessTypeNames(business.businessTypes).join(', ')}
              </span>
              <span>•</span>
              <div className="flex items-center gap-1">
                <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                <span className="font-medium text-gray-700">
                  {business.averageRating?.toFixed(1) || '0.0'}
                </span>
                <span className="text-gray-400">({business.reviewCount})</span>
              </div>
            </div>
          </div>
        </div>

        {/* 업체 설명 */}
        {business.description && (
          <div className="mt-4 rounded-xl bg-gray-50 p-3">
            <div className="flex items-center gap-2 text-sm font-medium text-gray-700">
              <Info className="h-4 w-4 text-[#3ec0c7]" />
              <span>업체 소개</span>
            </div>
            <p className="mt-2 text-sm leading-relaxed text-gray-600">
              {business.description}
            </p>
          </div>
        )}

        {/* 업체 공지 */}
        <div className="mt-3 rounded-xl bg-amber-50 p-3">
          <div className="flex items-center gap-2 text-sm font-medium text-amber-700">
            <Megaphone className="h-4 w-4" />
            <span>공지사항</span>
          </div>
          <p className="mt-2 text-sm leading-relaxed text-amber-600">
            예약 시간 10분 전까지 도착해주세요. 노쇼 시 예약이 취소될 수
            있습니다.
          </p>
        </div>
      </div>

      {/* 탭 메뉴 */}
      <div className="border-b">
        <div className="flex">
          {[
            { id: 'info', label: '정보' },
            { id: 'services', label: '서비스' },
            { id: 'reviews', label: '리뷰' },
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as TabType)}
              className={cn(
                'flex-1 py-3 text-sm font-medium transition-colors',
                activeTab === tab.id
                  ? 'border-b-2 border-[#3ec0c7] text-[#3ec0c7]'
                  : 'text-gray-500'
              )}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* 탭 컨텐츠 */}
      <div className="flex-1 px-4 py-4">
        {activeTab === 'services' && (
          <div>
            <div className="mb-4 flex items-center justify-between">
              <h2 className="font-semibold text-gray-900">
                서비스 ({menuList.totalCount})
              </h2>
            </div>

            {menuList.menus.length === 0 ? (
              <div className="py-8 text-center text-gray-500">
                <p>등록된 서비스가 없습니다</p>
              </div>
            ) : (
              <div className="space-y-4">
                {menuList.menus.map(menu => (
                  <ServiceCard
                    key={menu.menuId}
                    id={menu.menuId}
                    name={menu.serviceName}
                    description={menu.description || ''}
                    duration={menu.durationMinutes || 0}
                    price={menu.price}
                    isSelected={selectedServices.includes(menu.menuId)}
                    onToggle={toggleService}
                  />
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'info' && (
          <div className="space-y-4">
            <div>
              <h3 className="mb-2 font-semibold text-gray-900">주소</h3>
              <p className="text-sm text-gray-600">{business.address}</p>
            </div>
            <div>
              <h3 className="mb-2 font-semibold text-gray-900">연락처</h3>
              <p className="text-sm text-gray-600">{business.contactPhone}</p>
            </div>
            <div>
              <h3 className="mb-2 font-semibold text-gray-900">대표자</h3>
              <p className="text-sm text-gray-600">{business.ownerName}</p>
            </div>
          </div>
        )}

        {activeTab === 'reviews' && (
          <div className="py-8 text-center text-gray-500">
            <p>고객 리뷰가 표시됩니다</p>
          </div>
        )}
      </div>

      {/* 하단 예약 버튼 */}
      {selectedCount > 0 && (
        <div className="sticky bottom-16 border-t bg-white px-4 py-3">
          <div className="flex items-center gap-3">
            <div className="flex-1">
              <p className="text-sm text-gray-500">
                {selectedCount}개 서비스 선택됨
              </p>
              <p className="font-semibold text-gray-900">
                {formatPrice(totalPrice)}
              </p>
            </div>
            <Link
              href={`/places/${businessId}/reserve?menuIds=${selectedServices.join(',')}`}
            >
              <Button className="h-12 rounded-xl bg-[#3ec0c7] px-6 text-base font-semibold text-white hover:bg-[#35adb3]">
                지금 예약하기 →
              </Button>
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
