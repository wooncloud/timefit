'use client';

import { useMemo, useState } from 'react';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';

import type { CreateUpdateMenuRequest, Menu } from '@/types/menu/menu';
import type { Product } from '@/types/product/product';
import { useCategoryList } from '@/hooks/category/use-category-list';
import { useMenuDetail } from '@/hooks/menu/use-menu-detail';
import { useMenuList } from '@/hooks/menu/use-menu-list';
import { useBusinessStore } from '@/store/business-store';
import { ProductDetailForm } from '@/components/business/product/product-detail-form';
import { ProductEmptyState } from '@/components/business/product/product-empty-state';
import { ProductListPanel } from '@/components/business/product/product-list-panel';

// Menu → Product 변환 함수
function menuToProduct(menu: Menu): Product {
  return {
    id: menu.menuId,
    business_id: menu.businessId,
    service_name: menu.serviceName,
    category: menu.categoryName, // 백엔드에서 받은 카테고리 이름 사용
    description: menu.description,
    price: menu.price,
    duration_minutes: menu.durationMinutes || 60,
    menu_type: menu.orderType,
    image_url: menu.imageUrl,
    is_active: menu.isActive,
    created_at: menu.createdAt,
    updated_at: menu.updatedAt,
  };
}

// Product → CreateUpdateMenuRequest 변환 함수
function productToMenuRequest(
  product: Partial<Product>,
  businessType?: string
): CreateUpdateMenuRequest {
  return {
    businessType: (businessType || 'BD008') as any,
    categoryName: product.category || '', // 카테고리는 사용자가 선택한 값 사용
    serviceName: product.service_name || '',
    price: product.price || 0,
    description: product.description,
    imageUrl: product.image_url,
    orderType: product.menu_type || 'RESERVATION_BASED',
    durationMinutes: product.duration_minutes,
  };
}

export default function Page() {
  const [selectedMenuId, setSelectedMenuId] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);

  // 비즈니스 정보
  const { business } = useBusinessStore();
  const businessType = business?.businessTypes?.[0]; // 첫 번째 비즈니스 타입 사용

  // 메뉴 목록 조회
  const { menus, loading: listLoading, createMenu, refetch } = useMenuList();

  // 카테고리 목록 조회
  const { categories, loading: categoriesLoading } = useCategoryList();

  // 선택된 메뉴 상세 조회
  const {
    menu: selectedMenu,
    updateMenu,
    deleteMenu,
    toggleMenu,
  } = useMenuDetail(selectedMenuId);

  // Menu[] → Product[] 변환
  const products = useMemo(() => menus.map(menuToProduct), [menus]);

  // 선택된 Menu → Product 변환
  const selectedProduct = useMemo(
    () => (selectedMenu ? menuToProduct(selectedMenu) : null),
    [selectedMenu]
  );

  const handleSelectProduct = (product: Product | null) => {
    setSelectedMenuId(product?.id || null);
    setIsCreating(false);
  };

  const handleNewProduct = () => {
    setSelectedMenuId(null);
    setIsCreating(true);
  };

  const handleSaveProduct = async (productData: Partial<Product>) => {
    const menuRequest = productToMenuRequest(productData, businessType);

    if (selectedMenuId) {
      const success = await updateMenu(menuRequest);
      if (success) {
        toast.success('메뉴가 수정되었습니다.');
        await refetch();
      } else {
        toast.error('메뉴 수정에 실패했습니다.');
      }
    } else {
      const newMenu = await createMenu(menuRequest);
      if (newMenu) {
        toast.success('메뉴가 생성되었습니다.');
        await refetch();
        setSelectedMenuId(newMenu.menuId);
        setIsCreating(false);
      } else {
        toast.error('메뉴 생성에 실패했습니다.');
      }
    }
  };

  const handleDeleteProduct = async (_id: string) => {
    const success = await deleteMenu();
    if (success) {
      toast.success('메뉴가 삭제되었습니다.');
      await refetch(); // 목록 갱신
      setSelectedMenuId(null);
    } else {
      toast.error('메뉴 삭제에 실패했습니다.');
    }
  };

  const handleToggleActive = async () => {
    const success = await toggleMenu();
    if (success) {
      toast.success('메뉴 상태가 변경되었습니다.');
      await refetch(); // 목록 갱신
    } else {
      toast.error('메뉴 상태 변경에 실패했습니다.');
    }
  };

  // 로딩 상태 처리
  if (listLoading || categoriesLoading) {
    return (
      <div className="flex h-[calc(100vh-6rem)] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="flex h-[calc(100vh-6rem)]">
      {/* Left Panel */}
      <div className="w-80">
        <ProductListPanel
          products={products}
          selectedProductId={isCreating ? null : selectedProduct?.id}
          onSelectProduct={handleSelectProduct}
          onNewProduct={handleNewProduct}
        />
      </div>

      {/* Right Panel */}
      <div className="flex-1">
        {selectedProduct || isCreating ? (
          <ProductDetailForm
            key={isCreating ? 'creating-new' : selectedProduct?.id || 'new'}
            product={selectedProduct}
            categories={categories}
            onSave={handleSaveProduct}
            onDelete={handleDeleteProduct}
            onToggleActive={handleToggleActive}
          />
        ) : (
          <ProductEmptyState />
        )}
      </div>
    </div>
  );
}
