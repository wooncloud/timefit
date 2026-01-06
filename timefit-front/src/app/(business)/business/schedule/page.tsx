import { getOperatingHours } from '@/services/schedule/operating-hours-service';
import {
  mapOperatingHoursToBusinessHours,
  mapOperatingHoursToBookingSlotsMap,
} from '@/lib/data/schedule/map-operating-hours';
import { getCurrentUserFromSession } from '@/lib/session/server';

import { ScheduleClient } from './schedule-client';

export default async function SchedulePage() {
  const sessionUser = await getCurrentUserFromSession();

  if (!sessionUser) {
    throw new Error('세션 사용자 정보를 찾을 수 없습니다.');
  }

  const businessId = sessionUser.businesses?.[0]?.businessId;

  if (!businessId) {
    throw new Error('업체 ID를 찾을 수 없습니다.');
  }

  // ✅ Server Component에서 직접 데이터 페치
  const operatingHours = await getOperatingHours(businessId);

  // 영업시간 정보 변환
  const businessHours = mapOperatingHoursToBusinessHours(operatingHours);

  // 예약 슬롯 정보 변환
  const bookingSlotsMap = mapOperatingHoursToBookingSlotsMap(operatingHours);

  return (
    <ScheduleClient
      businessId={businessId}
      initialBusinessHours={businessHours}
      initialBookingSlotsMap={bookingSlotsMap}
    />
  );
}
