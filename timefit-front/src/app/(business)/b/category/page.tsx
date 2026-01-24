import { getCategoryList } from '@/services/category/category-service';
import { getBusinessContextWithType } from '@/lib/business/get-business-context';

import { CategoryClient } from './category-client';

export default async function Page() {
  const { businessId, businessType } = await getBusinessContextWithType();

  const categories = await getCategoryList(businessId);

  return (
    <CategoryClient
      initialCategories={categories}
      businessId={businessId}
      businessType={businessType}
    />
  );
}
