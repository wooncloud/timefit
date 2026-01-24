import Link from 'next/link';
import { Search, SlidersHorizontal, Star, MapPin, Sparkles, Dumbbell, Coffee, Heart } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

// 카테고리 더미 데이터
const categories = [
  { id: 'beauty', name: '뷰티', icon: Sparkles, color: '#3ec0c7' },
  { id: 'sports', name: '스포츠', icon: Dumbbell, color: '#f97316' },
  { id: 'cafe', name: '카페', icon: Coffee, color: '#8b5cf6' },
  { id: 'health', name: '건강', icon: Heart, color: '#ec4899' }
];

// 장소 더미 데이터
const places = [
  {
    id: '1',
    name: '젠 요가 스튜디오',
    description: '마음챙김과 균형',
    distance: '3.5km',
    rating: 4.8,
    image: '/images/placeholder.jpg',
    badge: '신규'
  },
  {
    id: '2',
    name: '페이드 마스터스',
    description: '클래식 컷 & 쉐이브',
    distance: '0.8km',
    rating: 4.6,
    image: '/images/placeholder.jpg',
    badge: null
  },
  {
    id: '3',
    name: '그린볼 컴퍼니',
    description: '유기농 & 신선한 음식',
    distance: '1.1km',
    rating: 4.9,
    image: '/images/placeholder.jpg',
    badge: '인기'
  },
  {
    id: '4',
    name: '럭스 스파 리트릿',
    description: '마사지 & 사우나',
    distance: '1.2km',
    rating: 4.9,
    image: '/images/placeholder.jpg',
    badge: null
  },
  {
    id: '5',
    name: '아이언 짐',
    description: '피트니스 & 웨이트',
    distance: '0.5km',
    rating: 4.8,
    image: '/images/placeholder.jpg',
    badge: null
  }
];

export default function HomePage() {
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
        <button className="absolute right-3 top-1/2 -translate-y-1/2 rounded-lg border border-gray-200 p-1.5">
          <SlidersHorizontal className="h-4 w-4 text-gray-500" />
        </button>
      </div>

      {/* 카테고리 */}
      <div className="mb-6">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">카테고리</h2>
          <Link href="/categories" className="text-sm font-medium text-[#3ec0c7]">
            전체보기
          </Link>
        </div>
        <div className="flex gap-4 overflow-x-auto pb-2">
          {categories.map((category) => {
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
                <span className="text-sm font-medium text-gray-600">{category.name}</span>
              </Link>
            );
          })}
        </div>
      </div>

      {/* 장소 리스트 */}
      <div>
        <h2 className="mb-4 text-lg font-semibold text-gray-900">추천 장소</h2>
        <div className="space-y-4">
          {places.map((place) => (
            <Link
              key={place.id}
              href={`/places/${place.id}`}
              className="flex gap-4 rounded-2xl border border-gray-100 bg-white p-3 shadow-sm transition-shadow hover:shadow-md"
            >
              {/* 이미지 */}
              <div className="relative h-24 w-24 flex-shrink-0 overflow-hidden rounded-xl bg-gray-200">
                {place.badge && (
                  <span
                    className={`absolute left-2 top-2 rounded-full px-2 py-0.5 text-xs font-semibold text-white ${
                      place.badge === '신규' ? 'bg-[#3ec0c7]' : 'bg-orange-500'
                    }`}
                  >
                    {place.badge}
                  </span>
                )}
              </div>

              {/* 정보 */}
              <div className="flex flex-1 flex-col justify-between py-1">
                <div>
                  <div className="flex items-center justify-between">
                    <h3 className="font-semibold text-gray-900">{place.name}</h3>
                    <div className="flex items-center gap-1">
                      <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                      <span className="text-sm font-medium text-gray-700">{place.rating}</span>
                    </div>
                  </div>
                  <p className="mt-0.5 text-sm text-gray-500">{place.description}</p>
                </div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-1 text-sm text-[#3ec0c7]">
                    <MapPin className="h-4 w-4" />
                    <span>{place.distance}</span>
                  </div>
                  <Button
                    size="sm"
                    variant="outline"
                    className="h-8 rounded-full border-gray-200 text-xs font-medium"
                  >
                    보기
                  </Button>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}
