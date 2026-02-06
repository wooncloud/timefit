import { getReservations } from '@/services/reservation/reservation-service';

import { BookingsClient } from './bookings-client';

export default async function BookingsPage() {
  // SSR: 서버에서 데이터 조회
  const reservations = await getReservations(undefined, 0, 20);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex items-center justify-between border-b bg-white px-4 py-4">
        <h1 className="text-xl font-bold text-gray-900">예약 내역</h1>
      </div>

      <BookingsClient initialData={reservations} />
    </div>
  );
}
