'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Calendar, ChevronLeft, Clock, Loader2 } from 'lucide-react';

import type { AuthUserProfile } from '@/types/auth/user';
import type { PublicBusinessDetail } from '@/types/business/business';
import type { Menu } from '@/types/customer/menu';
import { useCreateReservation } from '@/hooks/reservation/mutations/use-create-reservation';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

interface ReserveClientProps {
  business: PublicBusinessDetail;
  businessId: string;
  selectedMenus: Menu[];
  sessionUser: AuthUserProfile | null;
}

// 예약 가능한 시간 슬롯 (9시~18시, 1시간 단위)
const timeSlots = [
  '09:00',
  '10:00',
  '11:00',
  '12:00',
  '13:00',
  '14:00',
  '15:00',
  '16:00',
  '17:00',
  '18:00',
];

// 앞으로 14일간의 날짜 생성
const generateWeekDates = () => {
  const dates = [];
  const today = new Date();
  for (let i = 0; i < 14; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() + i);
    dates.push(date);
  }
  return dates;
};

const formatDate = (date: Date) => {
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  return {
    day: days[date.getDay()],
    date: date.getDate(),
    month: date.getMonth() + 1,
    full: date.toISOString().split('T')[0], // YYYY-MM-DD
  };
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('ko-KR').format(price) + '원';
};

export function ReserveClient({
  business,
  businessId,
  selectedMenus,
  sessionUser,
}: ReserveClientProps) {
  const router = useRouter();
  const { createReservation, loading } = useCreateReservation();

  const weekDates = generateWeekDates();
  const [selectedDate, setSelectedDate] = useState<string>(
    weekDates[0].toISOString().split('T')[0]
  );
  const [selectedTime, setSelectedTime] = useState<string>('');

  // 선택한 서비스들의 총 시간과 가격
  const totalDuration = selectedMenus.reduce(
    (sum, menu) => sum + (menu.durationMinutes || 0),
    0
  );
  const totalPrice = selectedMenus.reduce((sum, menu) => sum + menu.price, 0);

  const canSubmit = selectedDate && selectedTime && !loading;

  const handleSubmit = async () => {
    if (!canSubmit || !sessionUser) return;

    // TODO: 백엔드가 하나의 menuId만 받으므로, 여러 메뉴 선택 시 처리 방법 결정 필요
    // 현재는 첫 번째 메뉴만 예약 생성
    const firstMenu = selectedMenus[0];

    const reservation = await createReservation({
      businessId,
      menuId: firstMenu.menuId,
      bookingSlotId: null, // ONDEMAND_BASED 예약
      reservationDate: selectedDate, // YYYY-MM-DD
      reservationTime: `${selectedTime}:00`, // HH:mm:ss
      durationMinutes: firstMenu.durationMinutes || 60,
      totalPrice: firstMenu.price,
      customerName: sessionUser.name,
      customerPhone: sessionUser.phoneNumber?.replace(/-/g, '') || '', // 하이픈 제거
    });

    if (reservation) {
      // 예약 성공 시 업체 상세 페이지로 이동
      router.push(`/places/${businessId}`);
    }
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center gap-3 px-4 py-3">
        <Link
          href={`/places/${businessId}`}
          className="flex h-10 w-10 items-center justify-center"
        >
          <ChevronLeft className="h-6 w-6 text-gray-700" />
        </Link>
        <h1 className="text-lg font-semibold">예약하기</h1>
      </div>

      <div className="flex-1 px-4 py-4">
        {/* 업체 정보 */}
        <div className="mb-6 rounded-xl border border-gray-100 bg-gray-50 p-4">
          <h2 className="font-semibold text-gray-900">
            {business.businessName}
          </h2>
          <p className="mt-1 text-sm text-gray-600">{business.address}</p>
          <p className="mt-0.5 text-sm text-gray-600">
            {business.contactPhone}
          </p>
        </div>

        {/* 선택된 서비스 요약 */}
        <div className="mb-6">
          <h2 className="mb-3 text-sm font-semibold text-gray-900">
            선택한 서비스
          </h2>
          <div className="rounded-xl bg-gray-50 p-4">
            {selectedMenus.map((menu, index) => (
              <div
                key={menu.menuId}
                className={cn(
                  'flex items-center justify-between',
                  index > 0 && 'mt-3 border-t pt-3'
                )}
              >
                <div>
                  <p className="font-medium text-gray-900">
                    {menu.serviceName}
                  </p>
                  <p className="text-sm text-gray-500">
                    {menu.durationMinutes || 0}분
                  </p>
                </div>
                <p className="font-semibold text-gray-900">
                  {formatPrice(menu.price)}
                </p>
              </div>
            ))}
            <div className="mt-4 flex items-center justify-between border-t pt-4">
              <p className="font-semibold text-gray-900">
                총 {totalDuration}분
              </p>
              <p className="text-lg font-bold text-[#3ec0c7]">
                {formatPrice(totalPrice)}
              </p>
            </div>
          </div>
        </div>

        {/* 날짜 선택 */}
        <div className="mb-6">
          <div className="mb-3 flex items-center gap-2">
            <Calendar className="h-4 w-4 text-[#3ec0c7]" />
            <h2 className="text-sm font-semibold text-gray-900">날짜 선택</h2>
          </div>
          <div className="flex gap-2 overflow-x-auto pb-2">
            {weekDates.map(date => {
              const formatted = formatDate(date);
              const isSelected = selectedDate === formatted.full;
              const isToday = date.toDateString() === new Date().toDateString();

              return (
                <button
                  key={formatted.full}
                  onClick={() => setSelectedDate(formatted.full)}
                  className={cn(
                    'flex min-w-[60px] flex-col items-center rounded-xl border py-3 transition-all',
                    isSelected
                      ? 'border-[#3ec0c7] bg-[#3ec0c7] text-white'
                      : 'border-gray-200 bg-white text-gray-700 hover:border-[#3ec0c7]'
                  )}
                >
                  <span className="text-xs">{formatted.day}</span>
                  <span className="mt-1 text-lg font-bold">
                    {formatted.date}
                  </span>
                  {isToday && (
                    <span
                      className={cn(
                        'mt-1 text-[10px]',
                        isSelected ? 'text-white/80' : 'text-[#3ec0c7]'
                      )}
                    >
                      오늘
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        </div>

        {/* 시간 선택 */}
        <div className="mb-6">
          <div className="mb-3 flex items-center gap-2">
            <Clock className="h-4 w-4 text-[#3ec0c7]" />
            <h2 className="text-sm font-semibold text-gray-900">시간 선택</h2>
          </div>
          <div className="grid grid-cols-3 gap-2">
            {timeSlots.map(time => {
              const isSelected = selectedTime === time;

              return (
                <button
                  key={time}
                  onClick={() => setSelectedTime(time)}
                  className={cn(
                    'rounded-xl border py-3 text-sm font-medium transition-all',
                    isSelected
                      ? 'border-[#3ec0c7] bg-[#3ec0c7] text-white'
                      : 'border-gray-200 bg-white text-gray-700 hover:border-[#3ec0c7]'
                  )}
                >
                  {time}
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* 하단 예약 버튼 */}
      <div className="sticky bottom-16 border-t bg-white px-4 py-3">
        <Button
          onClick={handleSubmit}
          disabled={!canSubmit}
          className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3] disabled:bg-gray-300"
        >
          {loading ? (
            <Loader2 className="h-5 w-5 animate-spin" />
          ) : (
            '예약 확정하기'
          )}
        </Button>
      </div>
    </div>
  );
}
