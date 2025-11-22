'use client';

import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Plus } from 'lucide-react';
import type { Product } from '@/types/product/product';
import { productCategories } from '@/lib/constants/product-categories';
import { cn } from '@/lib/utils';
import { ProductListEmpty } from './product-list-empty';

interface ProductListPanelProps {
  products: Product[];
  selectedProductId?: string;
  onSelectProduct: (product: Product | null) => void;
  onNewProduct: () => void;
}

export function ProductListPanel({
  products,
  selectedProductId,
  onSelectProduct,
  onNewProduct,
}: ProductListPanelProps) {
  return (
    <div className="flex h-full flex-col border-r">
      <div className="border-b p-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">서비스 목록</h2>
          <Button size="sm" onClick={onNewProduct}>
            <Plus className="h-4 w-4" />
            새 서비스
          </Button>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto pt-2 pe-2">
        {products.length === 0 ? (
          <ProductListEmpty onNewProduct={onNewProduct} />
        ) : (
          <div className="space-y-1">
            {products.map((product) => (
              <button
                key={product.id}
                onClick={() => onSelectProduct(product)}
                className={cn(
                  'w-full rounded-lg border p-3 text-left transition-colors',
                  selectedProductId === product.id
                    ? 'border-primary bg-accent'
                    : 'border-transparent hover:bg-accent/50'
                )}
              >
                <div className="mb-2 flex items-start justify-between gap-2">
                  <h3 className="font-medium">{product.service_name}</h3>
                  {!product.is_active && (
                    <Badge variant="secondary" className="text-xs">
                      비활성
                    </Badge>
                  )}
                </div>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <span className="rounded bg-muted px-2 py-0.5 text-xs">
                    {productCategories[product.category]}
                  </span>
                  <span>·</span>
                  <span>{product.price.toLocaleString()}원</span>
                  <span>·</span>
                  <span>{product.duration_minutes}분</span>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
