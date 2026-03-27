import dayjs from 'dayjs';

import { getBusinessReservations } from '@/services/reservation/reservation-business-service';
import { getBusinessId } from '@/lib/business/get-business-context';

import { CalendarClient } from './calendar-client';

export default async function Page() {
  const businessId = await getBusinessId();

  // 이번 달 전체 예약 조회 (상태 필터 없이 전부)
  const today = dayjs();
  const startDate = today.startOf('month').format('YYYY-MM-DD');
  const endDate = today.endOf('month').format('YYYY-MM-DD');

  const { reservations } = await getBusinessReservations(businessId, {
    startDate,
    endDate,
    size: 100, // Server 요청 최대 값
  });

  return (
    <CalendarClient
      initialReservations={reservations}
      businessId={businessId}
    />
  );
}