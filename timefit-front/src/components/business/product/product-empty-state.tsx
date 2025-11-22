import { Package } from 'lucide-react';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';

export function ProductEmptyState() {
  return (
    <div className="flex h-full items-center justify-center">
      <Empty>
        <EmptyHeader>
          <EmptyMedia variant="icon">
            <Package className="h-12 w-12" />
          </EmptyMedia>
          <EmptyTitle>서비스를 선택해주세요</EmptyTitle>
          <EmptyDescription>
            좌측 목록에서 서비스를 선택하거나 새 서비스를 추가해주세요
          </EmptyDescription>
        </EmptyHeader>
      </Empty>
    </div>
  );
}
