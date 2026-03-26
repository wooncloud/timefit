'use client';

import dayjs from 'dayjs';
import { X } from 'lucide-react';

import type { BusinessReservationDetail, ReservationStatus } from '@/types/business/reservation';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

interface ReservationDetailModalProps {
  detail: BusinessReservationDetail | null;
  isOpen: boolean;
  onClose: () => void;
}

const STATUS_LABEL: Record<ReservationStatus, string> = {
  PENDING: '승인대기',
  CONFIRMED: '예약확정',
  COMPLETED: '완료',
  CANCELLED: '취소',
  REJECTED: '거절',
  NO_SHOW: '노쇼',
};

const STATUS_VARIANT: Record<ReservationStatus, 'default' | 'secondary' | 'outline' | 'destructive'> = {
  CONFIRMED: 'default',
  PENDING: 'secondary',
  COMPLETED: 'outline',
  CANCELLED: 'destructive',
  REJECTED: 'destructive',
  NO_SHOW: 'destructive',
};

function Row({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div className="flex justify-between py-2 border-b last:border-0">
      <span className="text-sm text-muted-foreground w-28 shrink-0">{label}</span>
      <span className="text-sm text-right">{value ?? '-'}</span>
    </div>
  );
}

export function ReservationDetailModal({
                                         detail,
                                         isOpen,
                                         onClose,
                                       }: ReservationDetailModalProps) {
  if (!isOpen || !detail) return null;

  return (
    // 배경 오버레이
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      onClick={onClose}
    >
      {/* 모달 본체 — 클릭 이벤트 버블링 차단 */}
      <div
        className="relative bg-background rounded-lg shadow-lg w-full max-w-lg max-h-[90vh] overflow-y-auto mx-4"
        onClick={e => e.stopPropagation()}
      >
        {/* 헤더 */}
        <div className="flex items-center justify-between p-4 border-b">
          <div>
            <h2 className="font-semibold text-lg">예약 상세</h2>
            <p className="text-sm text-muted-foreground">{detail.reservationNumber}</p>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        {/* 내용 */}
        <div className="p-4 space-y-6">

          {/* 예약 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">예약 정보</h3>
            <Row label="상태" value={
              <Badge variant={STATUS_VARIANT[detail.status]}>
                {STATUS_LABEL[detail.status]}
              </Badge>
            } />
            <Row
              label="예약 일시"
              value={`${dayjs(detail.reservationDate).format('YYYY.MM.DD')} ${detail.reservationTime.slice(0, 5)}`}
            />
            <Row label="서비스 시간" value={`${detail.reservationDuration}분`} />
            <Row label="예약 금액" value={`${detail.reservationPrice.toLocaleString()}원`} />
            <Row label="예약 접수일" value={dayjs(detail.createdAt).format('YYYY.MM.DD HH:mm')} />
            {detail.cancelledAt && (
              <Row label="취소 일시" value={dayjs(detail.cancelledAt).format('YYYY.MM.DD HH:mm')} />
            )}
            {detail.notes && (
              <Row label="메모" value={detail.notes} />
            )}
          </section>

          {/* 고객 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">고객 정보</h3>
            <Row label="이름" value={detail.customerName} />
            <Row label="연락처" value={detail.customerPhone} />
            {detail.customerEmail && (
              <Row label="이메일" value={detail.customerEmail} />
            )}
          </section>

          {/* 서비스 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">서비스 정보</h3>
            <Row label="서비스명" value={detail.menuServiceName} />
            {detail.menuCategoryCode && (
              <Row label="카테고리" value={detail.menuCategoryCode} />
            )}
            <Row label="서비스 금액" value={`${detail.menuPrice.toLocaleString()}원`} />
            {detail.menuDescription && (
              <Row label="설명" value={detail.menuDescription} />
            )}
            <Row
              label="유형"
              value={detail.menuOrderType === 'RESERVATION_BASED' ? '예약형' : '즉시형'}
            />
          </section>
        </div>
      </div>
    </div>
  );
}