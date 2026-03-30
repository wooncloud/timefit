import { getBusinessCustomers } from '@/services/business/customer-business-service';
import { getBusinessId } from '@/lib/business/get-business-context';

import { CustomersClient } from './customers-client';

export default async function Page() {
  const businessId = await getBusinessId();

  const { customers, pagination } = await getBusinessCustomers(businessId, {
    sortBy: 'LAST_VISIT',
  });

  return (
    <CustomersClient
      initialCustomers={customers}
      initialPagination={pagination}
      businessId={businessId}
    />
  );
}