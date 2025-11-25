'use client';

import { useState, useEffect } from 'react';
import type { Product } from '@/types/product/product';
import { ProductBasicInfoSection } from './sections/product-basic-info-section';
import { ProductReservationSection } from './sections/product-reservation-section';
import { ProductFormActions } from './sections/product-form-actions';

interface ProductDetailFormProps {
  product: Product | null;
  onSave: (product: Partial<Product>) => void;
  onDelete?: (id: string) => void;
  onToggleActive?: () => void;
}

export function ProductDetailForm({
  product,
  onSave,
  onDelete,
  onToggleActive,
}: ProductDetailFormProps) {
  const [formData, setFormData] = useState<Partial<Product>>(
    product || {
      service_name: '',
      category: 'HAIRCUT',
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
        category: 'HAIRCUT',
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
