import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import type { Product, MenuType } from '@/types/product/product';
import { menuTypes } from '@/lib/mock';

interface ProductReservationSectionProps {
  formData: Partial<Product>;
  onFormDataChange: (data: Partial<Product>) => void;
}

export function ProductReservationSection({
  formData,
  onFormDataChange,
}: ProductReservationSectionProps) {
  return (
    <div className="space-y-4 border-t pt-6">
      <div className="flex items-center gap-2">
        <div className="text-lg font-semibold">⏱️ 예약 설정</div>
      </div>

      <div className="space-y-3">
        <Label>서비스 유형</Label>
        <RadioGroup
          value={formData.menu_type}
          onValueChange={(value: MenuType) =>
            onFormDataChange({ ...formData, menu_type: value })
          }
        >
          {Object.entries(menuTypes).map(([key, label]) => (
            <div key={key} className="flex items-center space-x-2">
              <RadioGroupItem value={key} id={key} />
              <Label htmlFor={key} className="font-normal">
                {label} ({key})
              </Label>
            </div>
          ))}
        </RadioGroup>
      </div>

      <div className="space-y-2">
        <Label htmlFor="duration">소요 시간 (필수)</Label>
        <div className="flex items-center gap-2">
          <Input
            id="duration"
            type="number"
            value={formData.duration_minutes || 60}
            onChange={(e) =>
              onFormDataChange({
                ...formData,
                duration_minutes: Number(e.target.value),
              })
            }
            min="5"
            step="5"
            required
          />
          <span className="text-sm text-muted-foreground">분</span>
        </div>
      </div>
    </div>
  );
}
