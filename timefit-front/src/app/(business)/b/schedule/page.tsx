import { getOperatingHours } from '@/services/schedule/operating-hours-service';
import { getMenuList } from '@/services/menu/menu-service';
import { getBusinessId } from '@/lib/business/get-business-context';
import {
  mapOperatingHoursToBookingSlotsMap,
  mapOperatingHoursToBusinessHours,
} from '@/lib/data/schedule/map-operating-hours';

import { ScheduleClient } from './schedule-client';

export default async function SchedulePage() {
  const businessId = await getBusinessId();

  const [operatingHours, menuList] = await Promise.all([
    getOperatingHours(businessId),
    getMenuList(businessId),
  ]);

  const businessHours = mapOperatingHoursToBusinessHours(operatingHours);
  const bookingSlotsMap = mapOperatingHoursToBookingSlotsMap(operatingHours);
  const reservationMenus = menuList.menus.filter(
    m => m.orderType === 'RESERVATION_BASED' && m.isActive
  );

  return (
    <ScheduleClient
      businessId={businessId}
      initialBusinessHours={businessHours}
      initialBookingSlotsMap={bookingSlotsMap}
      initialOperatingHours={operatingHours}
      reservationMenus={reservationMenus}
    />
  );
}
