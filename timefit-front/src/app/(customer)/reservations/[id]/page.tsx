import Link from 'next/link';
import { AlertCircle } from 'lucide-react';

import { getReservationDetail } from '@/services/reservation/reservation-service';
import { getCurrentUserFromSession } from '@/lib/session/server';
import { Button } from '@/components/ui/button';

import { ReservationDetailClient } from './reservation-detail-client';

interface ReservationDetailPageProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function ReservationDetailPage({
  params,
}: ReservationDetailPageProps) {
  const { id: reservationId } = await params;

  try {
    const [reservation, sessionUser] = await Promise.all([
      getReservationDetail(reservationId),
      getCurrentUserFromSession(),
    ]);

    return (
      <ReservationDetailClient
        reservation={reservation}
        sessionUser={sessionUser}
      />
    );
  } catch (error) {
    console.error('예약 상세 로드 오류:', error);

    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <AlertCircle className="h-12 w-12 text-destructive" />
        <p className="mt-4 text-center text-gray-600">
          {error instanceof Error
            ? error.message
            : '예약 정보를 불러오는 데 실패했습니다.'}
        </p>
        <Link href="/reservations" className="mt-4">
          <Button variant="outline">예약 목록으로 돌아가기</Button>
        </Link>
      </div>
    );
  }
}
