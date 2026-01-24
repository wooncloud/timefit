'use client';

import Link from 'next/link';
import { Calendar, CheckCircle, Clock, MapPin } from 'lucide-react';

import { Button } from '@/components/ui/button';

// 예약 정보 더미 데이터 (실제로는 API 응답 또는 상태 관리에서 가져와야 함)
const reservationData = {
  reservationNumber: 'TF-20240124-001',
  placeName: '루미에르 웰니스 스튜디오',
  address: '서울시 강남구 테헤란로 123',
  date: '2024년 1월 25일 (목)',
  time: '14:00',
  services: [
    { name: '딥티슈 마사지', duration: 60, price: 90000 },
    { name: '아로마테라피 세션', duration: 30, price: 50000 },
  ],
  totalPrice: 140000,
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('ko-KR').format(price) + '원';
};

export default function ReserveSuccessPage() {
  return (
    <div className="flex flex-col bg-white px-4 py-8">
      {/* 성공 아이콘 */}
      <div className="flex flex-col items-center text-center">
        <div className="flex h-20 w-20 items-center justify-center rounded-full bg-[#e8f7f8]">
          <CheckCircle className="h-12 w-12 text-[#3ec0c7]" />
        </div>
        <h1 className="mt-4 text-xl font-bold text-gray-900">
          예약이 완료되었습니다!
        </h1>
        <p className="mt-2 text-sm text-gray-500">
          예약 번호:{' '}
          <span className="font-semibold text-[#3ec0c7]">
            {reservationData.reservationNumber}
          </span>
        </p>
      </div>

      {/* 예약 상세 정보 */}
      <div className="mt-8 rounded-2xl border border-gray-100 bg-gray-50 p-4">
        <h2 className="mb-4 font-semibold text-gray-900">예약 상세</h2>

        {/* 업체 정보 */}
        <div className="mb-4 border-b border-gray-200 pb-4">
          <p className="font-semibold text-gray-900">
            {reservationData.placeName}
          </p>
          <div className="mt-1 flex items-center gap-1 text-sm text-gray-500">
            <MapPin className="h-4 w-4" />
            <span>{reservationData.address}</span>
          </div>
        </div>

        {/* 날짜 및 시간 */}
        <div className="mb-4 flex gap-6 border-b border-gray-200 pb-4">
          <div className="flex items-center gap-2">
            <Calendar className="h-4 w-4 text-[#3ec0c7]" />
            <span className="text-sm text-gray-700">
              {reservationData.date}
            </span>
          </div>
          <div className="flex items-center gap-2">
            <Clock className="h-4 w-4 text-[#3ec0c7]" />
            <span className="text-sm text-gray-700">
              {reservationData.time}
            </span>
          </div>
        </div>

        {/* 서비스 목록 */}
        <div className="space-y-2">
          {reservationData.services.map((service, index) => (
            <div
              key={index}
              className="flex items-center justify-between text-sm"
            >
              <span className="text-gray-600">
                {service.name} ({service.duration}분)
              </span>
              <span className="font-medium text-gray-900">
                {formatPrice(service.price)}
              </span>
            </div>
          ))}
          <div className="mt-3 flex items-center justify-between border-t pt-3">
            <span className="font-semibold text-gray-900">총 결제금액</span>
            <span className="text-lg font-bold text-[#3ec0c7]">
              {formatPrice(reservationData.totalPrice)}
            </span>
          </div>
        </div>
      </div>

      {/* 안내 메시지 */}
      <div className="mt-6 rounded-xl bg-amber-50 p-4">
        <p className="text-sm leading-relaxed text-amber-700">
          예약 확인 문자가 발송되었습니다. 예약 시간 10분 전까지 방문해주세요.
          예약 취소는 24시간 전까지 가능합니다.
        </p>
      </div>

      {/* 버튼들 */}
      <div className="mt-8 space-y-3">
        <Link href="/bookings">
          <Button className="h-12 w-full rounded-xl bg-[#3ec0c7] text-base font-semibold text-white hover:bg-[#35adb3]">
            내 예약 확인하기
          </Button>
        </Link>
        <Link href="/">
          <Button
            variant="outline"
            className="h-12 w-full rounded-xl border-gray-200 text-base font-semibold text-gray-700"
          >
            홈으로 돌아가기
          </Button>
        </Link>
      </div>
    </div>
  );
}
