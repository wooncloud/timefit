import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';
import type { Product } from '@/types/product/product';

interface ProductFormActionsProps {
  product: Product | null;
  onDelete?: (id: string) => void;
  onToggleActive?: () => void;
}

export function ProductFormActions({
  product,
  onDelete,
  onToggleActive,
}: ProductFormActionsProps) {
  return (
    <div className="flex items-center justify-between p-4">
      <div className="flex items-center gap-4">
        {product && onDelete && (
          <Button
            type="button"
            variant="destructive"
            onClick={() => onDelete(product.id)}
          >
            삭제
          </Button>
        )}
      </div>
      <div className="flex items-center gap-4">
        {product && onToggleActive && (
          <div className="flex items-center space-x-2">
            <Switch
              id="active-mode"
              checked={product.is_active}
              onCheckedChange={onToggleActive}
            />
            <Label htmlFor="active-mode">
              {product.is_active ? '판매 중' : '판매 중지'}
            </Label>
          </div>
        )}
        <div className="flex gap-2">
          <Button type="submit">{product ? '수정하기' : '등록하기'}</Button>
        </div>
      </div>
    </div>
  );
}
