import { getOperatingHours } from '@/services/schedule/operating-hours-service';
import { getBusinessId } from '@/lib/business/get-business-context';
import {
  mapOperatingHoursToBookingSlotsMap,
  mapOperatingHoursToBusinessHours,
} from '@/lib/data/schedule/map-operating-hours';

import { ScheduleClient } from './schedule-client';

export default async function SchedulePage() {
  const businessId = await getBusinessId();

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
