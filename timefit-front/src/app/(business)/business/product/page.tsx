'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type {
  Product,
  CreateProductRequest,
  UpdateProductRequest,
} from '@/types/product/product';
import { useProducts } from '@/hooks/business/useProducts';
import { useBusinessStore } from '@/store/business-store';
import { ProductListPanel } from '@/components/business/product/product-list-panel';
import { ProductDetailForm } from '@/components/business/product/product-detail-form';
import { ProductEmptyState } from '@/components/business/product/product-empty-state';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';

export default function Page() {
  const business = useBusinessStore((state) => state.business);
  const businessId = business?.businessId || '';

  const {
    products,
    loading,
    error,
    createProduct,
    updateProduct,
    deleteProduct,
    saving,
    deleting,
  } = useProducts(businessId);

  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);

  const handleSelectProduct = (product: Product | null) => {
    setSelectedProduct(product);
    setIsCreating(false);
  };

  const handleNewProduct = () => {
    setSelectedProduct(null);
    setIsCreating(true);
  };

  const handleSaveProduct = async (productData: Partial<Product>) => {
    if (selectedProduct) {
      // 수정 모드
      const updateData: UpdateProductRequest = {
        serviceName: productData.serviceName || '',
        businessType: productData.businessType || 'BD008',
        categoryCode: productData.categoryCode || 'BEAUTY_HAIR_CUT',
        price: productData.price || 0,
        description: productData.description,
        durationMinutes: productData.durationMinutes || 60,
        imageUrl: productData.imageUrl,
        isActive: productData.isActive ?? true,
      };

      const success = await updateProduct(selectedProduct.id, updateData);
      if (success) {
        setSelectedProduct(null);
        setIsCreating(false);
      }
    } else {
      // 생성 모드
      const createData: CreateProductRequest = {
        businessType: productData.businessType || 'BD008',
        categoryCode: productData.categoryCode || 'BEAUTY_HAIR_CUT',
        serviceName: productData.serviceName || '',
        price: productData.price || 0,
        description: productData.description,
        orderType: productData.orderType || 'RESERVATION_BASED',
        durationMinutes: productData.durationMinutes || 60,
        imageUrl: productData.imageUrl,
      };

      const success = await createProduct(createData);
      if (success) {
        setSelectedProduct(null);
        setIsCreating(false);
      }
    }
  };

  const handleDeleteClick = (id: string) => {
    setDeleteTargetId(id);
    setConfirmOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!deleteTargetId) return;

    const success = await deleteProduct(deleteTargetId);
    if (success) {
      toast.success('서비스가 삭제되었습니다.');
      setSelectedProduct(null);
    }

    setConfirmOpen(false);
    setDeleteTargetId(null);
  };

  const handleCancel = () => {
    setSelectedProduct(null);
    setIsCreating(false);
  };

  // 로딩 상태 처리
  if (loading) {
    return (
      <div className="flex h-[calc(100vh-6rem)] items-center justify-center">
        <div className="text-muted-foreground">로딩 중...</div>
      </div>
    );
  }

  // 에러 상태 처리
  if (error) {
    return (
      <div className="flex h-[calc(100vh-6rem)] items-center justify-center">
        <div className="text-destructive">{error}</div>
      </div>
    );
  }

  // businessId가 없는 경우
  if (!businessId) {
    return (
      <div className="flex h-[calc(100vh-6rem)] items-center justify-center">
        <div className="text-muted-foreground">업체 정보를 찾을 수 없습니다.</div>
      </div>
    );
  }

  return (
    <>
      <div className="flex h-[calc(100vh-6rem)]">
        {/* Left Panel */}
        <div className="w-80">
          <ProductListPanel
            products={products || []}
            selectedProductId={selectedProduct?.id}
            onSelectProduct={handleSelectProduct}
            onNewProduct={handleNewProduct}
          />
        </div>

        {/* Right Panel */}
        <div className="flex-1">
          {selectedProduct || isCreating ? (
            <ProductDetailForm
              product={selectedProduct}
              onSave={handleSaveProduct}
              onCancel={handleCancel}
              onDelete={handleDeleteClick}
              saving={saving}
            />
          ) : (
            <ProductEmptyState />
          )}
        </div>
      </div>

      {/* 삭제 확인 다이얼로그 */}
      <ConfirmDialog
        open={confirmOpen}
        onOpenChange={setConfirmOpen}
        title="서비스 삭제"
        description="정말로 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
        confirmText="삭제"
        cancelText="취소"
        variant="destructive"
        onConfirm={handleConfirmDelete}
      />
    </>
  );
}
