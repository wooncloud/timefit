'use client';

import dayjs from 'dayjs';
import { Pencil, X } from 'lucide-react';

import type { BusinessCustomerDetail } from '@/types/business/customer-business';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

interface CustomerDetailModalProps {
  detail: BusinessCustomerDetail | null;
  isOpen: boolean;
  onClose: () => void;
  onMemo: (customerId: string, currentMemo: string | null) => void;
}

const STATUS_LABEL: Record<string, string> = {
  PENDING: '승인대기',
  CONFIRMED: '예약확정',
  COMPLETED: '완료',
  CANCELLED: '취소',
  REJECTED: '거절',
  NO_SHOW: '노쇼',
};

function Row({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="flex justify-between py-2 border-b last:border-0">
      <span className="text-sm text-muted-foreground w-24 shrink-0">{label}</span>
      <span className="text-sm text-right">{value ?? '-'}</span>
    </div>
  );
}

export function CustomerDetailModal({
                                      detail,
                                      isOpen,
                                      onClose,
                                      onMemo,
                                    }: CustomerDetailModalProps) {
  if (!isOpen || !detail) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      onClick={onClose}
    >
      <div
        className="relative bg-background rounded-lg shadow-lg w-full max-w-lg max-h-[90vh] overflow-y-auto mx-4"
        onClick={e => e.stopPropagation()}
      >
        {/* 헤더 */}
        <div className="flex items-center justify-between p-4 border-b">
          <h2 className="font-semibold text-lg">{detail.customerName}</h2>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="p-4 space-y-6">
          {/* 고객 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">고객 정보</h3>
            <Row label="연락처" value={detail.customerPhone} />
            {detail.customerEmail && <Row label="이메일" value={detail.customerEmail} />}
            <Row label="첫 방문일" value={dayjs(detail.firstVisitDate).format('YYYY.MM.DD')} />
            <Row label="최근 방문일" value={dayjs(detail.lastVisitDate).format('YYYY.MM.DD')} />
            <Row label="총 방문 횟수" value={`${detail.totalVisits}회`} />
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
                      <Badge variant="outline">{STATUS_LABEL[r.status] ?? r.status}</Badge>
                      <p className="text-muted-foreground mt-1">{r.reservationPrice.toLocaleString()}원</p>
                    </div>
                  </div>
                ))}
              </div>
            </section>
          )}
        </div>
      </div>
    </div>
  );
}