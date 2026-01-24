'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Search, Calendar, Clock, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

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

const statusConfig = {
  confirmed: {
    label: '확정',
    color: 'bg-[#3ec0c7] text-white'
  },
  pending: {
    label: '대기중',
    color: 'bg-orange-100 text-orange-600'
  },
  completed: {
    label: '이용완료',
    color: 'bg-gray-100 text-gray-600'
  },
  cancelled: {
    label: '취소됨',
    color: 'bg-red-100 text-red-600'
  }
};

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
              <div
                key={booking.id}
                className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm"
              >
                {/* 상단: 업체 정보 */}
                <div className="flex gap-3">
                  <div className="h-16 w-16 flex-shrink-0 rounded-xl bg-gray-200" />
                  <div className="flex-1">
                    <div className="flex items-start justify-between">
                      <div>
                        <h3 className="font-semibold text-gray-900">{booking.placeName}</h3>
                        <p className="text-sm text-gray-500">{booking.serviceName}</p>
                      </div>
                      <span
                        className={cn(
                          'rounded-full px-2.5 py-1 text-xs font-medium',
                          statusConfig[booking.status].color
                        )}
                      >
                        {statusConfig[booking.status].label}
                      </span>
                    </div>
                  </div>
                </div>

                {/* 날짜/시간 정보 */}
                <div className="mt-4 flex items-center gap-2 rounded-xl bg-gray-50 px-3 py-2.5">
                  {booking.status === 'completed' ? (
                    <CheckCircle className="h-5 w-5 text-gray-400" />
                  ) : booking.status === 'pending' ? (
                    <Clock className="h-5 w-5 text-orange-400" />
                  ) : (
                    <Calendar className="h-5 w-5 text-[#3ec0c7]" />
                  )}
                  <div>
                    <p className="text-xs text-gray-500">
                      {booking.status === 'completed'
                        ? '이용 완료'
                        : booking.status === 'pending'
                          ? '요청 시간'
                          : '예약 일시'}
                    </p>
                    <p className="text-sm font-medium text-gray-900">{booking.dateTime}</p>
                  </div>
                </div>

                {/* 버튼 */}
                <div className="mt-4 flex gap-2">
                  {booking.status === 'confirmed' && (
                    <>
                      <Button
                        variant="outline"
                        className="flex-1 rounded-xl border-gray-200 text-sm font-medium"
                      >
                        취소하기
                      </Button>
                      <Link href={`/places/${booking.placeId}`} className="flex-1">
                        <Button className="w-full rounded-xl bg-[#3ec0c7] text-sm font-medium text-white hover:bg-[#35adb3]">
                          상세보기
                        </Button>
                      </Link>
                    </>
                  )}

                  {booking.status === 'pending' && (
                    <>
                      <Button
                        variant="outline"
                        className="flex-1 rounded-xl border-gray-200 text-sm font-medium"
                      >
                        예약 취소
                      </Button>
                      <Link href={`/places/${booking.placeId}`} className="flex-1">
                        <Button
                          variant="outline"
                          className="w-full rounded-xl border-[#3ec0c7] text-sm font-medium text-[#3ec0c7]"
                        >
                          상세보기
                        </Button>
                      </Link>
                    </>
                  )}

                  {booking.status === 'completed' && (
                    <Link href={`/places/${booking.placeId}`} className="flex-1">
                      <Button
                        variant="outline"
                        className="w-full rounded-xl border-gray-200 text-sm font-medium"
                      >
                        상세보기
                      </Button>
                    </Link>
                  )}

                  {booking.status === 'cancelled' && (
                    <Link href={`/places/${booking.placeId}`} className="flex-1">
                      <Button
                        variant="outline"
                        className="w-full rounded-xl border-gray-200 text-sm font-medium"
                      >
                        다시 예약하기
                      </Button>
                    </Link>
                  )}
                </div>
              </div>
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
