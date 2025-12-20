'use client';

import { useEffect, useState } from 'react';
import { toast } from 'sonner';

import type { Category } from '@/types/category/category';
import type { Product } from '@/types/product/product';

import { ProductBasicInfoSection } from './sections/product-basic-info-section';
import { ProductFormActions } from './sections/product-form-actions';
import { ProductReservationSection } from './sections/product-reservation-section';

interface ProductDetailFormProps {
  product: Product | null;
  categories: Category[];
  onSave: (product: Partial<Product>) => void;
  onDelete?: (id: string) => void;
  onToggleActive?: () => void;
}

export function ProductDetailForm({
  product,
  categories,
  onSave,
  onDelete,
  onToggleActive,
}: ProductDetailFormProps) {
  const [formData, setFormData] = useState<Partial<Product>>(
    product || {
      service_name: '',
      category: '', // 사용자가 카테고리를 선택하도록 빈 값으로 시작
      price: 0,
      description: '',
      menu_type: 'RESERVATION_BASED',
      duration_minutes: 60,
      is_active: true,
    }
  );

  useEffect(() => {
    if (product) {
      setFormData(product);
    } else {
      setFormData({
        service_name: '',
        category: '', // 사용자가 카테고리를 선택하도록 빈 값으로 시작
        price: 0,
        description: '',
        menu_type: 'RESERVATION_BASED',
        duration_minutes: 60,
        is_active: true,
      });
    }
  }, [product]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Validation checks
    if (!formData.service_name?.trim()) {
      toast.error('서비스명을 입력해주세요.');
      return;
    }

    if (!formData.category?.trim()) {
      toast.error('카테고리를 선택해주세요.');
      return;
    }

    if (!formData.price || formData.price <= 0) {
      toast.error('가격을 입력해주세요.');
      return;
    }

    if (!formData.duration_minutes || formData.duration_minutes < 5) {
      toast.error('서비스 시간은 최소 5분 이상이어야 합니다.');
      return;
    }

    if (formData.duration_minutes > 1440) {
      toast.error('서비스 시간은 최대 1440분(24시간)을 초과할 수 없습니다.');
      return;
    }

    onSave(formData);
  };

  return (
    <div className="flex h-full flex-col">
      <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto">
        <ProductFormActions
          product={product}
          onDelete={onDelete}
          onToggleActive={onToggleActive}
        />

        <div className="space-y-6 p-4">
          <ProductBasicInfoSection
            formData={formData}
            categories={categories}
            onFormDataChange={setFormData}
          />

          <ProductReservationSection
            formData={formData}
            onFormDataChange={setFormData}
          />
        </div>
      </form>
    </div>
  );
}
