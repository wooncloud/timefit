import { Button } from '@/components/ui/button';
import type { Product } from '@/types/product/product';
import { Pencil, PlusCircle } from 'lucide-react';

interface ProductFormActionsProps {
  product: Product | null;
  onCancel: () => void;
  onDelete?: (id: string) => void;
  saving?: boolean;
}

export function ProductFormActions({
  product,
  onCancel,
  onDelete,
  saving = false,
}: ProductFormActionsProps) {
  return (
    <div className="flex items-center justify-between border-b p-4">
      <div className="flex items-center gap-2">
        {product ? (
          <Pencil className="h-6 w-6" />
        ) : (
          <PlusCircle className="h-6 w-6" />
        )}
        <h2 className="text-lg font-semibold">
          {product ? '서비스 수정하기' : '새 서비스 등록하기'}
        </h2>
      </div>
      <div className="flex gap-2">
        {product && onDelete && (
          <Button
            type="button"
            variant="destructive"
            onClick={() => onDelete(product.id)}
            disabled={saving}
          >
            삭제
          </Button>
        )}
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          disabled={saving}
        >
          취소
        </Button>
        <Button type="submit" disabled={saving}>
          {saving
            ? '저장 중...'
            : product
              ? '수정하기'
              : '등록하기'}
        </Button>
      </div>
    </div>
  );
}
