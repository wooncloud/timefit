import { getCategoryList } from '@/services/category/category-service';
import { getMenuList } from '@/services/menu/menu-service';
import { getBusinessContextWithType } from '@/lib/business/get-business-context';

import { ProductClient } from './product-client';

export default async function Page() {
  const { businessId, businessType } = await getBusinessContextWithType();

  // 메뉴와 카테고리를 병렬로 조회
  const [menus, categories] = await Promise.all([
    getMenuList(businessId),
    getCategoryList(businessId),
  ]);

  return (
    <ProductClient
      initialMenus={menus}
      initialCategories={categories}
      businessId={businessId}
      businessType={businessType}
    />
  );
}
