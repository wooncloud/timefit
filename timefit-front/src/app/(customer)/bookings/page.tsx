'use client';

import { useState } from 'react';
import { Search, Calendar } from 'lucide-react';
import { cn } from '@/lib/utils';
import { BookingCard } from '@/components/customer/cards/booking-card';

type BookingStatus = 'confirmed' | 'pending' | 'completed' | 'cancelled';
type TabType = 'upcoming' | 'completed' | 'cancelled';

interface Booking {
  id: string;
  placeId: string;
  placeName: string;
  serviceName: string;
  status: BookingStatus;
  dateTime: string;
  image?: string;
}

// 예약 더미 데이터
const bookingsData: Booking[] = [
  {
    id: '1',
    placeId: '1',
    placeName: '제니스 피트니스 스튜디오',
    serviceName: '1:1 필라테스 세션',
    status: 'confirmed',
    dateTime: '2024.01.20 14:00'
  },
  {
    id: '2',
    placeId: '2',
    placeName: '아쿠아 웰니스 센터',
    serviceName: '수중 테라피 패스',
    status: 'pending',
    dateTime: '2024.01.22 10:00'
  },
  {
    id: '3',
    placeId: '3',
    placeName: '아이언 피스트 복싱',
    serviceName: 'HIIT 복싱 클래스',
    status: 'completed',
    dateTime: '2024.01.15 18:30'
  },
  {
    id: '4',
    placeId: '4',
    placeName: '럭스 스파 리트릿',
    serviceName: '딥티슈 마사지',
    status: 'cancelled',
    dateTime: '2024.01.10 16:00'
  }
];



export default function BookingsPage() {
  const [activeTab, setActiveTab] = useState<TabType>('upcoming');

  const getFilteredBookings = () => {
    switch (activeTab) {
      case 'upcoming':
        return bookingsData.filter((b) => b.status === 'confirmed' || b.status === 'pending');
      case 'completed':
        return bookingsData.filter((b) => b.status === 'completed');
      case 'cancelled':
        return bookingsData.filter((b) => b.status === 'cancelled');
      default:
        return [];
    }
  };

  const filteredBookings = getFilteredBookings();

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4 py-4">
        <h1 className="text-xl font-bold text-gray-900">내 예약</h1>
        <button className="flex h-10 w-10 items-center justify-center">
          <Search className="h-5 w-5 text-gray-600" />
        </button>
      </div>

      {/* 탭 */}
      <div className="border-b px-4">
        <div className="flex">
          {[
            { id: 'upcoming', label: '이용 예정' },
            { id: 'completed', label: '이용 완료' },
            { id: 'cancelled', label: '취소됨' }
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

      {/* 예약 리스트 */}
      <div className="flex-1 px-4 py-4">
        {filteredBookings.length > 0 ? (
          <div className="space-y-4">
            {filteredBookings.map((booking) => (
              <BookingCard
                key={booking.id}
                id={booking.id}
                placeId={booking.placeId}
                placeName={booking.placeName}
                serviceName={booking.serviceName}
                status={booking.status}
                dateTime={booking.dateTime}
                image={booking.image}
              />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-16">
            <Calendar className="h-12 w-12 text-gray-300" />
            <p className="mt-4 text-gray-500">예약 내역이 없습니다</p>
          </div>
        )}
      </div>
    </div>
  );
}
