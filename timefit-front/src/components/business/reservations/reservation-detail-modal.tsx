'use client';

import dayjs from 'dayjs';

import type { BusinessReservationDetail } from '@/types/business/reservation';
import { Badge } from '@/components/ui/badge';
import { DetailRow } from '@/components/ui/detail-row';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

import {
  RESERVATION_STATUS_LABEL,
  RESERVATION_STATUS_VARIANT,
} from '@/lib/constants/reservation-status';

interface ReservationDetailModalProps {
  detail: BusinessReservationDetail | null;
  isOpen: boolean;
  onClose: () => void;
}

export function ReservationDetailModal({
                                         detail,
                                         isOpen,
                                         onClose,
                                       }: ReservationDetailModalProps) {
  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>예약 상세</DialogTitle>
          {detail && (
            <p className="text-sm text-muted-foreground">{detail.reservationNumber}</p>
          )}
        </DialogHeader>

        {detail && (
          <div className="space-y-6">

          {/* 예약 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">예약 정보</h3>
            <DetailRow label="상태" value={
              <Badge variant={RESERVATION_STATUS_VARIANT[detail.status]}>
                {RESERVATION_STATUS_LABEL[detail.status]}
              </Badge>
            } />
            <DetailRow
              label="예약 일시"
              value={`${dayjs(detail.reservationDate).format('YYYY.MM.DD')} ${detail.reservationTime.slice(0, 5)}`}
            />
            <DetailRow label="서비스 시간" value={`${detail.reservationDuration}분`} />
            <DetailRow label="예약 금액" value={`${detail.reservationPrice.toLocaleString()}원`} />
            <DetailRow label="예약 접수일" value={dayjs(detail.createdAt).format('YYYY.MM.DD HH:mm')} />
            {detail.cancelledAt && (
              <DetailRow label="취소 일시" value={dayjs(detail.cancelledAt).format('YYYY.MM.DD HH:mm')} />
            )}
            {detail.notes && (
              <DetailRow label="메모" value={detail.notes} />
            )}
          </section>

          {/* 고객 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">고객 정보</h3>
            <DetailRow label="이름" value={detail.customerName} />
            <DetailRow label="연락처" value={detail.customerPhone} />
            {detail.customerEmail && (
              <DetailRow label="이메일" value={detail.customerEmail} />
            )}
          </section>

          {/* 서비스 정보 */}
          <section>
            <h3 className="text-sm font-semibold mb-2">서비스 정보</h3>
            <DetailRow label="서비스명" value={detail.menuServiceName} />
            {detail.menuCategoryCode && (
              <DetailRow label="카테고리" value={detail.menuCategoryCode} />
            )}
            <DetailRow label="서비스 금액" value={`${detail.menuPrice.toLocaleString()}원`} />
            {detail.menuDescription && (
              <DetailRow label="설명" value={detail.menuDescription} />
            )}
            <DetailRow
              label="유형"
              value={detail.menuOrderType === 'RESERVATION_BASED' ? '예약형' : '즉시형'}
            />
          </section>
        </div>
        )}
      </DialogContent>
    </Dialog>
  );
}