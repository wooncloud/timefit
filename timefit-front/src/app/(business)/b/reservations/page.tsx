import { getBusinessReservations } from '@/services/reservation/reservation-business-service';
import { getBusinessId } from '@/lib/business/get-business-context';

import { ReservationsClient } from './reservations-client';

export default async function Page() {
  const businessId = await getBusinessId();

  const { reservations, pagination } = await getBusinessReservations(businessId);

  return (
    <ReservationsClient
      initialReservations={reservations}
      initialPagination={pagination}
      businessId={businessId}
    />
  );
}