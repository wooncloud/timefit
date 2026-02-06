'use client';

import { useState } from 'react';
import { Clock, Search, X } from 'lucide-react';

import { PlaceCard } from '@/components/customer/cards/place-card';
import { Input } from '@/components/ui/input';

// 최근 검색어 더미 데이터
const recentSearches = [
  { id: '1', text: '근처 요가' },
  { id: '2', text: '남성 헤어컷' },
  { id: '3', text: '마사지' },
];

// 검색 결과 더미 데이터
const searchResults = [
  {
    id: '1',
    name: '젠 요가 스튜디오',
    description: '마음챙김과 균형',
    rating: 4.8,
  },
  {
    id: '2',
    name: '페이드 마스터스',
    description: '클래식 컷 & 쉐이브',
    rating: 4.6,
  },
  {
    id: '3',
    name: '럭스 스파 리트릿',
    description: '마사지 & 사우나',
    rating: 4.9,
  },
  {
    id: '4',
    name: '그린볼 컴퍼니',
    description: '유기농 & 신선한 음식',
    rating: 4.9,
  },
];

export default function SearchPage() {
  const [query, setQuery] = useState('');
  const [searches, setSearches] = useState(recentSearches);

  const isSearching = query.trim().length > 0;

  const handleClearAll = () => {
    setSearches([]);
  };

  const handleRemoveSearch = (id: string) => {
    setSearches(searches.filter(s => s.id !== id));
  };

  const handleRecentClick = (text: string) => {
    setQuery(text);
  };

  // 검색 결과 필터링 (간단한 더미 로직)
  const filteredResults = searchResults.filter(
    place =>
      place.name.toLowerCase().includes(query.toLowerCase()) ||
      place.description.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="bg-white px-4 py-4">
      {/* 검색바 */}
      <div className="relative mb-6">
        <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-[#3ec0c7]" />
        <Input
          type="text"
          placeholder="오늘 어디로 가고 싶으세요?"
          value={query}
          onChange={e => setQuery(e.target.value)}
          className="h-12 rounded-xl border-gray-200 pl-10 pr-10"
          autoFocus
        />
        {query && (
          <button
            onClick={() => setQuery('')}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
          >
            <X className="h-5 w-5" />
          </button>
        )}
      </div>

      {!isSearching ? (
        /* 최근 검색어 */
        <div>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-xs font-semibold uppercase tracking-wider text-gray-500">
              최근 검색어
            </h2>
            {searches.length > 0 && (
              <button
                onClick={handleClearAll}
                className="text-sm font-medium text-[#3ec0c7]"
              >
                전체 삭제
              </button>
            )}
          </div>

          {searches.length > 0 ? (
            <div className="space-y-1">
              {searches.map(search => (
                <div
                  key={search.id}
                  className="flex items-center justify-between rounded-lg py-3"
                >
                  <button
                    onClick={() => handleRecentClick(search.text)}
                    className="flex items-center gap-3 text-left"
                  >
                    <Clock className="h-5 w-5 text-gray-400" />
                    <span className="text-gray-700">{search.text}</span>
                  </button>
                  <button
                    onClick={() => handleRemoveSearch(search.id)}
                    className="p-1 text-gray-400 hover:text-gray-600"
                  >
                    <X className="h-4 w-4" />
                  </button>
                </div>
              ))}
            </div>
          ) : (
            <p className="py-8 text-center text-sm text-gray-400">
              최근 검색어가 없습니다
            </p>
          )}
        </div>
      ) : (
        /* 검색 결과 */
        <div>
          <h2 className="mb-4 text-xs font-semibold uppercase tracking-wider text-gray-500">
            검색 결과 ({filteredResults.length})
          </h2>

          {filteredResults.length > 0 ? (
            <div className="space-y-3">
              {filteredResults.map(place => (
                <PlaceCard
                  key={place.id}
                  id={place.id}
                  name={place.name}
                  description={place.description}
                  rating={place.rating}
                  showAction={false}
                />
              ))}
            </div>
          ) : (
            <div className="py-12 text-center">
              <p className="text-gray-500">검색 결과가 없습니다</p>
              <p className="mt-1 text-sm text-gray-400">
                다른 키워드로 검색해보세요
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
