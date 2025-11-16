import { PackagePlus } from "lucide-react";
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";

export function ProductListEmptyState() {
  return (
    <Empty className="h-full border-0">
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <PackagePlus />
        </EmptyMedia>
        <EmptyTitle>등록된 서비스가 없습니다</EmptyTitle>
        <EmptyDescription>
          새 서비스 버튼을 눌러
          <br />
          첫 서비스를 추가해보세요
        </EmptyDescription>
      </EmptyHeader>
    </Empty>
  );
}

