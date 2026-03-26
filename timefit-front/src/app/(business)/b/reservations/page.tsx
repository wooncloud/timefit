import dayjs from 'dayjs';

import {
  getBusinessReservations,
  getBusinessReservationStats,
} from '@/services/reservation/reservation-business-service';
import { getBusinessId } from '@/lib/business/get-business-context';

import { ReservationsClient } from './reservations-client';

export default async function Page() {
  const businessId = await getBusinessId();

  // 기본값: 오늘 기준 ±15일
  const today = dayjs();
  const startDate = today.subtract(15, 'day').format('YYYY-MM-DD');
  const endDate = today.add(15, 'day').format('YYYY-MM-DD');

  // 목록과 통계 동일한 날짜 기준으로 병렬 조회
  const [{ reservations, pagination }, stats] = await Promise.all([
    getBusinessReservations(businessId, { startDate, endDate }),
    getBusinessReservationStats(businessId, { startDate, endDate }),
  ]);

  return (
      <ReservationsClient
          initialReservations={reservations}
          initialPagination={pagination}
          initialStats={stats}
          businessId={businessId}
      />
  );
}