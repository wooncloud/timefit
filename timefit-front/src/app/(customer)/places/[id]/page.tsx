'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { ChevronLeft, Share2, Heart, Star, Info, Megaphone } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { ServiceCard } from '@/components/customer/cards/service-card';

// 업체 더미 데이터
const placeData = {
  id: '1',
  name: '루미에르 웰니스 스튜디오',
  category: '뷰티 & 건강',
  rating: 4.9,
  description:
    '루미에르 웰니스 스튜디오는 도심 속 힐링을 제공하는 프리미엄 웰니스 공간입니다. 전문 테라피스트들이 고객 맞춤형 서비스를 제공합니다.',
  notice: '예약 시간 10분 전까지 도착해주세요. 노쇼 시 예약이 취소될 수 있습니다.',
  images: ['/images/place1.jpg', '/images/place2.jpg', '/images/place3.jpg']
};

// 서비스 더미 데이터
const services = [
  {
    id: '1',
    name: '딥티슈 마사지',
    description: '근육과 결합 조직의 심한 긴장을 완화합니다.',
    duration: 60,
    price: 90000
  },
  {
    id: '2',
    name: '오가닉 페이셜 글로우',
    description: '천연 유기농 성분으로 피부를 리쥬버네이팅합니다.',
    duration: 45,
    price: 75000
  },
  {
    id: '3',
    name: '아로마테라피 세션',
    description: '에센셜 오일을 사용하여 신체적, 정서적 웰빙을 개선합니다.',
    duration: 30,
    price: 50000
  },
  {
    id: '4',
    name: '스웨디시 마사지',
    description: '부드러운 압력으로 전신 이완과 혈액순환을 촉진합니다.',
    duration: 50,
    price: 70000
  }
];

type TabType = 'info' | 'services' | 'reviews';

export default function PlaceDetailPage() {
  const params = useParams();
  const [activeTab, setActiveTab] = useState<TabType>('services');
  const [selectedServices, setSelectedServices] = useState<string[]>([]);
  const [isWishlisted, setIsWishlisted] = useState(false);

  const toggleService = (serviceId: string) => {
    setSelectedServices((prev) =>
      prev.includes(serviceId) ? prev.filter((id) => id !== serviceId) : [...prev, serviceId]
    );
  };

  const selectedCount = selectedServices.length;
  const totalPrice = services
    .filter((s) => selectedServices.includes(s.id))
    .reduce((sum, s) => sum + s.price, 0);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('ko-KR').format(price) + '원';
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 상단 이미지 영역 */}
      <div className="relative">
        {/* 메인 이미지 */}
        <div className="flex gap-1">
          <div className="h-56 flex-1 bg-gray-300" />
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
          <button className="flex h-10 w-10 items-center justify-center rounded-full bg-white/80 backdrop-blur-sm">
            <Share2 className="h-5 w-5 text-gray-700" />
          </button>
        </div>
      </div>

      {/* 업체 정보 */}
      <div className="px-4 py-4">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-xl font-bold text-gray-900">{placeData.name}</h1>
            <div className="mt-1 flex items-center gap-2 text-sm text-gray-500">
              <span>{placeData.category}</span>
              <span>•</span>
              <div className="flex items-center gap-1">
                <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                <span className="font-medium text-gray-700">{placeData.rating}</span>
              </div>
            </div>
          </div>
          <button
            onClick={() => setIsWishlisted(!isWishlisted)}
            className="flex h-10 w-10 items-center justify-center"
          >
            <Heart
              className={cn(
                'h-6 w-6 transition-colors',
                isWishlisted ? 'fill-red-500 text-red-500' : 'text-gray-400'
              )}
            />
          </button>
        </div>

        {/* 업체 설명 */}
        <div className="mt-4 rounded-xl bg-gray-50 p-3">
          <div className="flex items-center gap-2 text-sm font-medium text-gray-700">
            <Info className="h-4 w-4 text-[#3ec0c7]" />
            <span>업체 소개</span>
          </div>
          <p className="mt-2 text-sm leading-relaxed text-gray-600">{placeData.description}</p>
        </div>

        {/* 업체 공지 */}
        <div className="mt-3 rounded-xl bg-amber-50 p-3">
          <div className="flex items-center gap-2 text-sm font-medium text-amber-700">
            <Megaphone className="h-4 w-4" />
            <span>공지사항</span>
          </div>
          <p className="mt-2 text-sm leading-relaxed text-amber-600">{placeData.notice}</p>
        </div>
      </div>

      {/* 탭 메뉴 */}
      <div className="border-b">
        <div className="flex">
          {[
            { id: 'info', label: '정보' },
            { id: 'services', label: '서비스' },
            { id: 'reviews', label: '리뷰' }
          ].map((tab) => (
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
              <h2 className="font-semibold text-gray-900">인기 서비스</h2>
              <button className="text-sm font-medium text-[#3ec0c7]">전체보기</button>
            </div>

            <div className="space-y-4">
              {services.map((service) => (
                <ServiceCard
                  key={service.id}
                  id={service.id}
                  name={service.name}
                  description={service.description}
                  duration={service.duration}
                  price={service.price}
                  isSelected={selectedServices.includes(service.id)}
                  onToggle={toggleService}
                />
              ))}
            </div>
          </div>
        )}

        {activeTab === 'info' && (
          <div className="py-8 text-center text-gray-500">
            <p>영업시간, 위치 등 상세 정보가 표시됩니다</p>
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
              <p className="text-sm text-gray-500">{selectedCount}개 서비스 선택됨</p>
              <p className="font-semibold text-gray-900">{formatPrice(totalPrice)}</p>
            </div>
            <Link href={`/places/${params.id}/reserve`}>
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
