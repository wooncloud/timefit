'use client';

import { useMemo, useState } from 'react';
import { toast } from 'sonner';

import type { CreateUpdateMenuRequest, Menu, MenuListResponse } from '@/types/menu/menu';
import type { Product } from '@/types/product/product';
import type { CategoryListResponse, Category } from '@/types/category/category';
import { useCreateMenu } from '@/hooks/menu/mutations/use-create-menu';
import { useUpdateMenu } from '@/hooks/menu/mutations/use-update-menu';
import { useDeleteMenu } from '@/hooks/menu/mutations/use-delete-menu';
import { useToggleMenu } from '@/hooks/menu/mutations/use-toggle-menu';
import { ProductDetailForm } from '@/components/business/product/product-detail-form';
import { ProductEmptyState } from '@/components/business/product/product-empty-state';
import { ProductListPanel } from '@/components/business/product/product-list-panel';

// Menu → Product 변환 함수
function menuToProduct(menu: Menu): Product {
    return {
        id: menu.menuId,
        business_id: menu.businessId,
        service_name: menu.serviceName,
        category: menu.categoryName,
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
        categoryName: product.category || '',
        serviceName: product.service_name || '',
        price: product.price || 0,
        description: product.description,
        imageUrl: product.image_url,
        orderType: product.menu_type || 'RESERVATION_BASED',
        durationMinutes: product.duration_minutes,
    };
}

interface ProductClientProps {
    initialMenus: MenuListResponse;
    initialCategories: CategoryListResponse;
    businessId: string;
    businessType: string;
}

export function ProductClient({
    initialMenus,
    initialCategories,
    businessId,
    businessType,
}: ProductClientProps) {
    const [selectedMenuId, setSelectedMenuId] = useState<string | null>(null);
    const [isCreating, setIsCreating] = useState(false);

    // Mutation hooks
    const { createMenu } = useCreateMenu(businessId);
    const { updateMenu } = useUpdateMenu(businessId);
    const { deleteMenu } = useDeleteMenu(businessId);
    const { toggleMenu } = useToggleMenu(businessId);

    // 서버에서 받은 초기 데이터 사용
    const menus = initialMenus.menus;
    const categories = initialCategories.categories;

    // Menu[] → Product[] 변환
    const products = useMemo(() => menus.map(menuToProduct), [menus]);

    // 선택된 메뉴 찾기
    const selectedMenu = useMemo(
        () => menus.find(m => m.menuId === selectedMenuId) || null,
        [menus, selectedMenuId]
    );

    // 선택된 Menu → Product 변환
    const selectedProduct = useMemo(
        () => (selectedMenu ? menuToProduct(selectedMenu) : null),
        [selectedMenu]
    );

    const refreshPage = () => {
        window.location.reload();
    };

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
            const result = await updateMenu(selectedMenuId, menuRequest);
            if (result) {
                refreshPage();
            }
        } else {
            const newMenu = await createMenu(menuRequest);
            if (newMenu) {
                refreshPage();
            }
        }
    };

    const handleDeleteProduct = async (_id: string) => {
        if (!selectedMenuId) return;

        const success = await deleteMenu(selectedMenuId);
        if (success) {
            refreshPage();
        }
    };

    const handleToggleActive = async () => {
        if (!selectedMenuId) return;

        const result = await toggleMenu(selectedMenuId);
        if (result) {
            refreshPage();
        }
    };

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
