'use client';

import { useState } from 'react';
import { mockProducts } from '@/lib/mock';
import type { Product } from '@/types/product/product';
import { ProductListPanel } from '@/components/business/product/product-list-panel';
import { ProductDetailForm } from '@/components/business/product/product-detail-form';
import { ProductEmptyState } from '@/components/business/product/product-empty-state';

export default function Page() {
  const [products, setProducts] = useState<Product[]>(mockProducts);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isCreating, setIsCreating] = useState(false);

  const handleSelectProduct = (product: Product | null) => {
    setSelectedProduct(product);
    setIsCreating(false);
  };

  const handleNewProduct = () => {
    setSelectedProduct(null);
    setIsCreating(true);
  };

  const handleSaveProduct = (productData: Partial<Product>) => {
    if (selectedProduct) {
      // 수정
      setProducts(
        products.map((p) =>
          p.id === selectedProduct.id
            ? { ...selectedProduct, ...productData, updated_at: new Date().toISOString() }
            : p
        )
      );
    } else {
      // 생성
      const newProduct: Product = {
        id: `${products.length + 1}`,
        business_id: 'b1',
        service_name: productData.service_name || '',
        category: productData.category || 'HAIRCUT',
        description: productData.description,
        price: productData.price || 0,
        duration_minutes: productData.duration_minutes || 60,
        menu_type: productData.menu_type || 'RESERVATION_BASED',
        image_url: productData.image_url,
        is_active: productData.is_active ?? true,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
      };
      setProducts([...products, newProduct]);
    }
    setIsCreating(false);
    setSelectedProduct(null);
  };

  const handleDeleteProduct = (id: string) => {
    if (confirm('정말 삭제하시겠습니까?')) {
      setProducts(products.filter((p) => p.id !== id));
      setSelectedProduct(null);
    }
  };

  const handleCancel = () => {
    setSelectedProduct(null);
    setIsCreating(false);
  };

  return (
    <div className="flex h-[calc(100vh-6rem)]">
      {/* Left Panel */}
      <div className="w-80">
        <ProductListPanel
          products={products}
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
            onDelete={handleDeleteProduct}
          />
        ) : (
          <ProductEmptyState />
        )}
      </div>
    </div>
  );
}
