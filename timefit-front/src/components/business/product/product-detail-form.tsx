'use client';

import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import type { Product } from '@/types/product/product';
import { ProductBasicInfoSection } from './sections/product-basic-info-section';
import { ProductReservationSection } from './sections/product-reservation-section';
import { ProductBookingSlotSection } from './sections/product-booking-slot-section';
import { ProductFormActions } from './sections/product-form-actions';

interface ProductDetailFormProps {
  product: Product | null;
  onSave: (product: Partial<Product>) => void;
  onCancel: () => void;
  onDelete?: (id: string) => void;
}

export function ProductDetailForm({
  product,
  onSave,
  onCancel,
  onDelete,
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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div className="flex h-full flex-col">
      <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto">
        <ProductFormActions
          product={product}
          onCancel={onCancel}
          onDelete={onDelete}
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

          {formData.menu_type === 'RESERVATION_BASED' && (
            <ProductBookingSlotSection />
          )}
        </div>
      </form>
    </div>
  );
}
