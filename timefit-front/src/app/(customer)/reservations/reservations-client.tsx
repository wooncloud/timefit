'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Calendar, ChevronRight, Clock } from 'lucide-react';

import type { AuthUserProfile } from '@/types/auth/user';
import type {
  CustomerReservationItem,
  PaginationInfo,
} from '@/types/customer/reservation';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

interface ReservationsClientProps {
  initialReservations: CustomerReservationItem[];
  pagination: PaginationInfo;
  sessionUser: AuthUserProfile | null;
}

// 예약 상태 한글 변환
const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PENDING':
      return '대기 중';
    case 'CONFIRMED':
      return '확정';
    case 'REJECTED':
      return '거절됨';
    case 'CANCELLED':
      return '취소됨';
    case 'COMPLETED':
      return '완료';
    case 'NO_SHOW':
      return '노쇼';
    default:
      return status;
  }
};

// 예약 상태 색상
const getStatusColor = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800';
    case 'CONFIRMED':
      return 'bg-green-100 text-green-800';
    case 'REJECTED':
      return 'bg-red-100 text-red-800';
    case 'CANCELLED':
      return 'bg-gray-100 text-gray-800';
    case 'COMPLETED':
      return 'bg-blue-100 text-blue-800';
    case 'NO_SHOW':
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export function ReservationsClient({
  initialReservations,
  pagination,
  sessionUser,
}: ReservationsClientProps) {
  const [reservations] = useState(initialReservations);

  if (!sessionUser) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <p className="text-sm text-muted-foreground">로그인이 필요합니다.</p>
        <Link href="/signin" className="mt-4">
          <Button>로그인하기</Button>
        </Link>
      </div>
    );
  }

  if (reservations.length === 0) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <p className="text-sm text-muted-foreground">예약 내역이 없습니다.</p>
        <Link href="/" className="mt-4">
          <Button>홈으로 가기</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <div className="sticky top-0 z-10 border-b bg-white px-4 py-4">
        <h1 className="text-xl font-bold">내 예약</h1>
      </div>

      {/* Reservations List */}
      <div className="divide-y">
        {reservations.map(reservation => (
          <Link
            key={reservation.reservationId}
            href={`/reservations/${reservation.reservationId}`}
            className="block px-4 py-4 transition-colors hover:bg-gray-50"
          >
            <div className="flex items-start gap-3">
              {/* Business Logo */}
              {reservation.businessLogoUrl ? (
                <img
                  src={reservation.businessLogoUrl}
                  alt={reservation.businessName}
                  className="h-16 w-16 rounded-lg object-cover"
                />
              ) : (
                <div className="flex h-16 w-16 items-center justify-center rounded-lg bg-gray-100">
                  <span className="text-2xl font-bold text-gray-400">
                    {reservation.businessName.charAt(0)}
                  </span>
                </div>
              )}

              {/* Reservation Info */}
              <div className="min-w-0 flex-1">
                <div className="mb-1 flex items-center justify-between gap-2">
                  <h3 className="truncate text-base font-semibold">
                    {reservation.businessName}
                  </h3>
                  <span
                    className={cn(
                      'whitespace-nowrap rounded px-2 py-1 text-xs font-medium',
                      getStatusColor(reservation.status)
                    )}
                  >
                    {getStatusLabel(reservation.status)}
                  </span>
                </div>

                <div className="space-y-1">
                  <div className="flex items-center gap-1 text-sm text-gray-600">
                    <Calendar className="h-4 w-4" />
                    <span>{reservation.reservationDate}</span>
                    <Clock className="ml-2 h-4 w-4" />
                    <span>
                      {reservation.reservationTime.substring(0, 5)} (
                      {reservation.reservationDuration}분)
                    </span>
                  </div>

                  <p className="text-sm font-medium">
                    {reservation.reservationPrice.toLocaleString()}원
                  </p>
                </div>
              </div>

              {/* Arrow */}
              <ChevronRight className="h-5 w-5 flex-shrink-0 text-gray-400" />
            </div>
          </Link>
        ))}
      </div>

      {/* Pagination Info */}
      <div className="px-4 py-6 text-center text-sm text-gray-500">
        {pagination.totalElements}개의 예약 중 {reservations.length}개 표시
      </div>
    </div>
  );
}
