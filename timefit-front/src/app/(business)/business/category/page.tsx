import { getCategoryList } from '@/services/category/category-service';
import { getCurrentUserFromSession } from '@/lib/session/server';

import { CategoryClient } from './category-client';

export default async function Page() {
  const sessionUser = await getCurrentUserFromSession();

  if (!sessionUser) {
    throw new Error('세션 사용자 정보를 찾을 수 없습니다.');
  }

  const businessId = sessionUser.businesses?.[0]?.businessId;
  const businessType = sessionUser.businesses?.[0]?.businessTypes?.[0];

  if (!businessId) {
    throw new Error('업체 ID를 찾을 수 없습니다.');
  }

  if (!businessType) {
    throw new Error('업체 타입 정보를 찾을 수 없습니다.');
  }

  const categories = await getCategoryList(businessId);

  return (
    <CategoryClient
      initialCategories={categories}
      businessId={businessId}
      businessType={businessType}
    />
  );
}
