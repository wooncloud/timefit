import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import type {
  Product,
  BusinessTypeCode,
  ServiceCategoryCode,
} from '@/types/product/product';
import { useBusinessStore } from '@/store/business-store';
import {
  BUSINESS_TYPE_NAMES,
  getCategoriesByBusinessType,
} from '@/lib/constants/categories';
import { useMemo } from 'react';

interface ProductBasicInfoSectionProps {
  formData: Partial<Product>;
  onFormDataChange: (data: Partial<Product>) => void;
}

export function ProductBasicInfoSection({
  formData,
  onFormDataChange,
}: ProductBasicInfoSectionProps) {
  const business = useBusinessStore((state) => state.business);

  // 업체의 첫 번째 businessType을 기본값으로 사용
  const businessTypes = business?.businessTypes || [];
  const availableBusinessTypes = businessTypes as BusinessTypeCode[];

  // 선택된 businessType에 따라 카테고리 목록 필터링
  const availableCategories = useMemo(() => {
    if (!formData.businessType) return [];
    return getCategoriesByBusinessType(formData.businessType);
  }, [formData.businessType]);

  // businessType 변경 시 카테고리도 리셋
  const handleBusinessTypeChange = (businessType: BusinessTypeCode) => {
    const categories = getCategoriesByBusinessType(businessType);
    const firstCategory = categories[0];

    onFormDataChange({
      ...formData,
      businessType,
      categoryCode: firstCategory?.code || ('' as ServiceCategoryCode),
      categoryName: firstCategory?.displayName || '',
    });
  };

  // 카테고리 변경 시
  const handleCategoryChange = (categoryCode: ServiceCategoryCode) => {
    const category = availableCategories.find((cat) => cat.code === categoryCode);
    onFormDataChange({
      ...formData,
      categoryCode,
      categoryName: category?.displayName || '',
    });
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        <div className="text-lg font-semibold">📋 기본 정보</div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="serviceName">서비스명</Label>
        <Input
          id="serviceName"
          value={formData.serviceName || ''}
          onChange={(e) =>
            onFormDataChange({ ...formData, serviceName: e.target.value })
          }
          placeholder="디자인 컷"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="businessType">업종 (대분류)</Label>
        <Select
          value={formData.businessType}
          onValueChange={handleBusinessTypeChange}
        >
          <SelectTrigger id="businessType">
            <SelectValue placeholder="업종을 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            {availableBusinessTypes.map((type) => (
              <SelectItem key={type} value={type}>
                {BUSINESS_TYPE_NAMES[type]}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="categoryCode">카테고리 (중분류)</Label>
        <Select
          value={formData.categoryCode}
          onValueChange={handleCategoryChange}
          disabled={!formData.businessType || availableCategories.length === 0}
        >
          <SelectTrigger id="categoryCode">
            <SelectValue placeholder="카테고리를 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            {availableCategories.map((category) => (
              <SelectItem key={category.code} value={category.code}>
                {category.displayName}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        {!formData.businessType && (
          <p className="text-xs text-muted-foreground">
            먼저 업종을 선택해주세요.
          </p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="price">가격</Label>
        <div className="flex items-center gap-2">
          <Input
            id="price"
            type="number"
            value={formData.price || 0}
            onChange={(e) =>
              onFormDataChange({ ...formData, price: Number(e.target.value) })
            }
            onFocus={(e) => e.target.select()}
            min="0"
            required
          />
          <span className="text-sm text-muted-foreground">원</span>
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="durationMinutes">소요 시간</Label>
        <div className="flex items-center gap-2">
          <Input
            id="durationMinutes"
            type="number"
            value={formData.durationMinutes || 60}
            onChange={(e) =>
              onFormDataChange({
                ...formData,
                durationMinutes: Number(e.target.value),
              })
            }
            onFocus={(e) => e.target.select()}
            min="1"
            required
          />
          <span className="text-sm text-muted-foreground">분</span>
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">설명</Label>
        <Textarea
          id="description"
          value={formData.description || ''}
          onChange={(e) =>
            onFormDataChange({ ...formData, description: e.target.value })
          }
          placeholder="트렌디한 헤어 스타일링"
          rows={3}
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="image">이미지</Label>
        <Input id="image" type="file" accept="image/*" disabled />
        <p className="text-xs text-muted-foreground">
          이미지 업로드 기능은 곧 추가됩니다.
        </p>
      </div>
    </div>
  );
}
