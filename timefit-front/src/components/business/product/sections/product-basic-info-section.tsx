import type { Category } from '@/types/category/category';
import type { Product } from '@/types/product/product';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';

interface ProductBasicInfoSectionProps {
  formData: Partial<Product>;
  categories: Category[];
  onFormDataChange: (data: Partial<Product>) => void;
}

export function ProductBasicInfoSection({
  formData,
  categories,
  onFormDataChange,
}: ProductBasicInfoSectionProps) {
  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        <div className="text-lg font-semibold">📋 기본 정보</div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="service_name">서비스명</Label>
        <Input
          id="service_name"
          value={formData.service_name || ''}
          onChange={e =>
            onFormDataChange({ ...formData, service_name: e.target.value })
          }
          placeholder="디자인 컷"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="category">카테고리</Label>
        <Select
          value={formData.category || ''}
          onValueChange={value =>
            onFormDataChange({ ...formData, category: value })
          }
        >
          <SelectTrigger id="category">
            <SelectValue placeholder="카테고리를 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            {categories
              .filter(cat => cat.isActive)
              .map(category => (
                <SelectItem
                  key={category.categoryId}
                  value={category.categoryName}
                >
                  {category.categoryName}
                </SelectItem>
              ))}
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-2">
        <Label htmlFor="price">가격</Label>
        <div className="flex items-center gap-2">
          <Input
            id="price"
            type="number"
            value={formData.price || 0}
            onChange={e =>
              onFormDataChange({ ...formData, price: Number(e.target.value) })
            }
            min="0"
            step="1000"
            required
          />
          <span className="text-sm text-muted-foreground">원</span>
        </div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">설명</Label>
        <Textarea
          id="description"
          value={formData.description || ''}
          onChange={e =>
            onFormDataChange({ ...formData, description: e.target.value })
          }
          placeholder="트렌디한 헤어 스타일링"
          rows={3}
        />
      </div>
    </div>
  );
}
