import { PackageOpen, Plus } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

interface ProductListEmptyProps {
  onNewProduct: () => void;
}

export function ProductListEmpty({ onNewProduct }: ProductListEmptyProps) {
  return (
    <div className="flex h-full items-center justify-center p-4">
      <Empty>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <PackageOpen className="h-10 w-10" />
          </EmptyMedia>
          <EmptyTitle>등록된 서비스가 없습니다</EmptyTitle>
          <EmptyDescription>
            새 서비스 버튼을 눌러 서비스를 추가해보세요
          </EmptyDescription>
        </EmptyHeader>
        <EmptyContent>
          <Button size="sm" onClick={onNewProduct}>
            <Plus className="h-4 w-4" />새 서비스 추가
          </Button>
        </EmptyContent>
      </Empty>
    </div>
  );
}
