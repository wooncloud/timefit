'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { Calendar, ChevronLeft, Clock, MessageSquare } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';

// 선택된 서비스 더미 데이터 (실제로는 이전 페이지에서 전달받아야 함)
const selectedServices = [
  { id: '1', name: '딥티슈 마사지', duration: 60, price: 90000 },
  { id: '3', name: '아로마테라피 세션', duration: 30, price: 50000 },
];

// 예약 가능한 시간 슬롯 더미 데이터
const timeSlots = [
  '09:00',
  '10:00',
  '11:00',
  '13:00',
  '14:00',
  '15:00',
  '16:00',
  '17:00',
  '18:00',
];

// 이번 주 날짜 생성
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

const weekDates = generateWeekDates();

const formatDate = (date: Date) => {
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  return {
    day: days[date.getDay()],
    date: date.getDate(),
    month: date.getMonth() + 1,
    full: date.toISOString().split('T')[0],
  };
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('ko-KR').format(price) + '원';
};

export default function ReservePage() {
  const params = useParams();
  const router = useRouter();
  const [selectedDate, setSelectedDate] = useState<string>(
    weekDates[0].toISOString().split('T')[0]
  );
  const [selectedTime, setSelectedTime] = useState<string>('');
  const [request, setRequest] = useState('');

  const totalDuration = selectedServices.reduce(
    (sum, s) => sum + s.duration,
    0
  );
  const totalPrice = selectedServices.reduce((sum, s) => sum + s.price, 0);

  const canSubmit = selectedDate && selectedTime;

  const handleSubmit = () => {
    if (!canSubmit) return;
    // TODO: 실제 예약 API 호출
    router.push(`/places/${params.id}/reserve/success`);
  };

  return (
    <div className="flex flex-col bg-white">
      {/* 헤더 */}
      <div className="flex items-center gap-3 px-4 py-3">
        <Link
          href={`/places/${params.id}`}
          className="flex h-10 w-10 items-center justify-center"
        >
          <ChevronLeft className="h-6 w-6 text-gray-700" />
        </Link>
        <h1 className="text-lg font-semibold">예약하기</h1>
      </div>

      <div className="flex-1 px-4 py-4">
        {/* 선택된 서비스 요약 */}
        <div className="mb-6">
          <h2 className="mb-3 text-sm font-semibold text-gray-900">
            선택한 서비스
          </h2>
          <div className="rounded-xl bg-gray-50 p-4">
            {selectedServices.map((service, index) => (
              <div
                key={service.id}
                className={cn(
                  'flex items-center justify-between',
                  index > 0 && 'mt-3 border-t pt-3'
                )}
              >
                <div>
                  <p className="font-medium text-gray-900">{service.name}</p>
                  <p className="text-sm text-gray-500">{service.duration}분</p>
                </div>
                <p className="font-semibold text-gray-900">
                  {formatPrice(service.price)}
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

        {/* 요청사항 */}
        <div className="mb-6">
          <div className="mb-3 flex items-center gap-2">
            <MessageSquare className="h-4 w-4 text-[#3ec0c7]" />
            <h2 className="text-sm font-semibold text-gray-900">
              요청사항 (선택)
            </h2>
          </div>
          <Textarea
            placeholder="업체에 전달할 요청사항을 입력해주세요"
            value={request}
            onChange={e => setRequest(e.target.value)}
            className="min-h-[100px] resize-none rounded-xl border-gray-200"
          />
        </div>
      </div>

      {/* 하단 예약 버튼 */}
      <div className="sticky bottom-16 border-t bg-white px-4 py-3">
        <Button
          onClick={handleSubmit}
          disabled={!canSubmit}
          className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3] disabled:bg-gray-300"
        >
          예약 확정하기
        </Button>
      </div>
    </div>
  );
}
