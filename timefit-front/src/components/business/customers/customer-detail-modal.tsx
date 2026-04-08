'use client';

import dayjs from 'dayjs';
import { Pencil } from 'lucide-react';

import type { BusinessCustomerDetail } from '@/types/business/customer-business';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { DetailRow } from '@/components/ui/detail-row';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { RESERVATION_STATUS_LABEL } from '@/lib/constants/reservation-status';

interface CustomerDetailModalProps {
  detail: BusinessCustomerDetail | null;
  isOpen: boolean;
  onClose: () => void;
  onMemo: (customerId: string, currentMemo: string | null) => void;
}

export function CustomerDetailModal({
                                      detail,
                                      isOpen,
                                      onClose,
                                      onMemo,
                                    }: CustomerDetailModalProps) {
  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{detail?.customerName}</DialogTitle>
        </DialogHeader>

        {detail && (
          <div className="p-4 space-y-6">
            {/* 고객 정보 */}
            <section>
              <h3 className="text-sm font-semibold mb-2">고객 정보</h3>
              <DetailRow label="연락처" value={detail.customerPhone} />
              {detail.customerEmail && <DetailRow label="이메일" value={detail.customerEmail} />}
              <DetailRow label="첫 방문일" value={dayjs(detail.firstVisitDate).format('YYYY.MM.DD')} />
              <DetailRow label="최근 방문일" value={dayjs(detail.lastVisitDate).format('YYYY.MM.DD')} />
              <DetailRow label="총 방문 횟수" value={`${detail.totalVisits}회`} />
            </section>

            {/* 메모 */}
            <section>
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-semibold">메모</h3>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => onMemo(detail.customerId, detail.memo)}
                >
                  <Pencil className="h-3 w-3 mr-1" />
                  편집
                </Button>
              </div>
              <p className="text-sm text-muted-foreground">
                {detail.memo || '메모 없음'}
              </p>
            </section>

            {/* 최근 예약 이력 */}
            {detail.recentReservations.length > 0 && (
              <section>
                <h3 className="text-sm font-semibold mb-2">최근 예약 이력</h3>
                <div className="space-y-2">
                  {detail.recentReservations.map(r => (
                    <div key={r.reservationId} className="flex items-center justify-between p-2 rounded border text-sm">
                      <div>
                        <p className="font-medium">{r.menuServiceName}</p>
                        <p className="text-muted-foreground">
                          {dayjs(r.reservationDate).format('YYYY.MM.DD')} {r.reservationTime.slice(0, 5)}
                        </p>
                      </div>
                      <div className="text-right">
                        <Badge variant="outline">{RESERVATION_STATUS_LABEL[r.status] ?? r.status}</Badge>
                        <p className="text-muted-foreground mt-1">{r.reservationPrice.toLocaleString()}원</p>
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}