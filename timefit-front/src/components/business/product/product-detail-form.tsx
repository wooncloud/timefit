'use client';

import { useState, useMemo } from 'react';
import type {
  Product,
  BusinessTypeCode,
  ServiceCategoryCode,
} from '@/types/product/product';
import { useBusinessStore } from '@/store/business-store';
import { getCategoriesByBusinessType } from '@/lib/constants/categories';
import { ProductBasicInfoSection } from './sections/product-basic-info-section';
// import { ProductReservationSection } from './sections/product-reservation-section';
// import { ProductBookingSlotSection } from './sections/product-booking-slot-section';
import { ProductFormActions } from './sections/product-form-actions';

interface ProductDetailFormProps {
  product: Product | null;
  onSave: (product: Partial<Product>) => void;
  onCancel: () => void;
  onDelete?: (id: string) => void;
  saving?: boolean;
}

export function ProductDetailForm({
  product,
  onSave,
  onCancel,
  onDelete,
  saving = false,
}: ProductDetailFormProps) {
  const business = useBusinessStore((state) => state.business);

  // 업체의 첫 번째 businessType으로 기본값 설정
  const defaultBusinessType = useMemo(() => {
    const types = business?.businessTypes || [];
    return types.length > 0 ? (types[0] as BusinessTypeCode) : 'BD008';
  }, [business?.businessTypes]);

  const defaultCategory = useMemo(() => {
    const categories = getCategoriesByBusinessType(defaultBusinessType);
    return categories[0];
  }, [defaultBusinessType]);

  const [formData, setFormData] = useState<Partial<Product>>(
    product || {
      serviceName: '',
      businessType: defaultBusinessType,
      categoryCode:
        (defaultCategory?.code as ServiceCategoryCode) || 'BEAUTY_HAIR_CUT',
      categoryName: defaultCategory?.displayName || '헤어 커트',
      price: 0,
      description: '',
      orderType: 'RESERVATION_BASED',
      durationMinutes: 60,
      isActive: true,
    }
  );

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div className="flex h-full flex-col">
      <form onSubmit={handleSubmit} className="flex h-full flex-col">
        <ProductFormActions
          product={product}
          onCancel={onCancel}
          onDelete={onDelete}
          saving={saving}
        />

        <div className="flex-1 space-y-6 overflow-y-auto p-4">
          <ProductBasicInfoSection
            formData={formData}
            onFormDataChange={setFormData}
          />

          {/* <ProductReservationSection
            formData={formData}
            onFormDataChange={setFormData}
          />

          {formData.orderType === 'RESERVATION_BASED' && (
            <ProductBookingSlotSection />
          )} */}
        </div>
      </form>
    </div>
  );
}
