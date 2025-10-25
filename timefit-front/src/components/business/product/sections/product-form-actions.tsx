import { Button } from '@/components/ui/button';
import type { Product } from '@/types/product/product';

interface ProductFormActionsProps {
  product: Product | null;
  onCancel: () => void;
  onDelete?: (id: string) => void;
}

export function ProductFormActions({
  product,
  onCancel,
  onDelete,
}: ProductFormActionsProps) {
  return (
    <div className="flex items-center justify-between p-4">
      <div>
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
      <div className="flex gap-2">
        <Button type="button" variant="outline" onClick={onCancel}>
          취소
        </Button>
        <Button type="submit">{product ? '수정하기' : '등록하기'}</Button>
      </div>
    </div>
  );
}
