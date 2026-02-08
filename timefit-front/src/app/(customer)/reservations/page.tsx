import Link from 'next/link';
import { AlertCircle } from 'lucide-react';

import { getMyReservations } from '@/services/reservation/reservation-service';
import { getCurrentUserFromSession } from '@/lib/session/server';
import { Button } from '@/components/ui/button';

import { ReservationsClient } from './reservations-client';

export default async function ReservationsPage() {
  try {
    const [reservationList, sessionUser] = await Promise.all([
      getMyReservations({ page: 0, size: 20 }),
      getCurrentUserFromSession(),
    ]);

    return (
      <ReservationsClient
        initialReservations={reservationList.reservations}
        pagination={reservationList.pagination}
        sessionUser={sessionUser}
      />
    );
  } catch (error) {
    console.error('예약 목록 로드 오류:', error);

    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <AlertCircle className="h-12 w-12 text-destructive" />
        <p className="mt-4 text-center text-gray-600">
          {error instanceof Error
            ? error.message
            : '예약 목록을 불러오는 데 실패했습니다.'}
        </p>
        <Link href="/" className="mt-4">
          <Button variant="outline">홈으로 돌아가기</Button>
        </Link>
      </div>
    );
  }
}
