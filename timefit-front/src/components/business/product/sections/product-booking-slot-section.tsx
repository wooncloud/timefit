import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Calendar } from 'lucide-react';

export function ProductBookingSlotSection() {
  return (
    <div className="space-y-4 border-t pt-6">
      <div className="flex items-center gap-2">
        <div className="text-lg font-semibold">📅 BookingSlot 생성 설정</div>
      </div>

      <div className="space-y-2">
        <Label htmlFor="slot_interval">슬롯 간격</Label>
        <div className="flex items-center gap-2">
          <Input
            id="slot_interval"
            type="number"
            defaultValue={60}
            min="5"
            step="5"
          />
          <span className="text-sm text-muted-foreground">분</span>
        </div>
      </div>

      <div className="space-y-2">
        <Label>생성 기간</Label>
        <div className="flex items-center gap-2">
          <Input type="date" defaultValue="2025-10-25" />
          <span className="text-muted-foreground">~</span>
          <Input type="date" defaultValue="2025-12-31" />
        </div>
      </div>

      <div className="space-y-2 rounded-md bg-muted p-3 text-sm text-muted-foreground">
        <div className="flex items-start gap-2">
          <Calendar className="mt-0.5 h-4 w-4 flex-shrink-0" />
          <div className="space-y-1">
            <p>영업시간 기준으로 자동 생성됩니다.</p>
            <p>휴게시간은 자동으로 제외됩니다.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
