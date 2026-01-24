import { getBusinessDetail } from '@/services/business/business-detail-service';
import { getBusinessId } from '@/lib/business/get-business-context';

import { SettingsClient } from './settings-client';

export default async function Page() {
  const businessId = await getBusinessId();

  const business = await getBusinessDetail(businessId);

  return <SettingsClient initialBusiness={business} businessId={businessId} />;
}
