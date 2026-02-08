'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import {
  Calendar,
  ChevronLeft,
  Clock,
  CreditCard,
  FileText,
  MapPin,
  Phone,
  User,
} from 'lucide-react';

import type { AuthUserProfile } from '@/types/auth/user';
import type { CustomerReservation } from '@/types/customer/reservation';
import { useCancelReservation } from '@/hooks/reservation/mutations/use-cancel-reservation';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';

interface ReservationDetailClientProps {
  reservation: CustomerReservation;
  sessionUser: AuthUserProfile | null;
}

// 예약 상태 한글 변환
const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PENDING':
      return '대기 중';
    case 'CONFIRMED':
      return '확정';
    case 'REJECTED':
      return '거절됨';
    case 'CANCELLED':
      return '취소됨';
    case 'COMPLETED':
      return '완료';
    case 'NO_SHOW':
      return '노쇼';
    default:
      return status;
  }
};

// 예약 상태 색상
const getStatusColor = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800';
    case 'CONFIRMED':
      return 'bg-green-100 text-green-800';
    case 'REJECTED':
      return 'bg-red-100 text-red-800';
    case 'CANCELLED':
      return 'bg-gray-100 text-gray-800';
    case 'COMPLETED':
      return 'bg-blue-100 text-blue-800';
    case 'NO_SHOW':
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

export function ReservationDetailClient({
  reservation,
  sessionUser,
}: ReservationDetailClientProps) {
  const router = useRouter();
  const { cancelReservation, loading } = useCancelReservation();
  const [confirmOpen, setConfirmOpen] = useState(false);

  const handleCancel = async () => {
    const success = await cancelReservation(reservation.reservationId);
    if (success) {
      router.push('/reservations');
    }
  };

  if (!sessionUser) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <p className="text-sm text-muted-foreground">로그인이 필요합니다.</p>
        <Link href="/signin" className="mt-4">
          <Button>로그인하기</Button>
        </Link>
      </div>
    );
  }

  // 취소 가능 여부 (PENDING 또는 CONFIRMED 상태만 취소 가능)
  const canCancel =
    reservation.status === 'PENDING' || reservation.status === 'CONFIRMED';

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <div className="sticky top-0 z-10 border-b bg-white px-4 py-4">
        <div className="flex items-center gap-3">
          <Link href="/reservations">
            <Button variant="ghost" size="icon">
              <ChevronLeft className="h-5 w-5" />
            </Button>
          </Link>
          <h1 className="text-xl font-bold">예약 상세</h1>
        </div>
      </div>

      {/* Content */}
      <div className="space-y-6 px-4 py-6">
        {/* Status Badge */}
        <div className="flex justify-center">
          <span
            className={cn(
              'rounded-full px-4 py-2 text-sm font-medium',
              getStatusColor(reservation.status)
            )}
          >
            {getStatusLabel(reservation.status)}
          </span>
        </div>

        {/* Reservation Number */}
        <div className="text-center">
          <p className="text-sm text-gray-500">예약 번호</p>
          <p className="text-lg font-bold">{reservation.reservationNumber}</p>
        </div>

        {/* Business Info */}
        <div className="space-y-3 rounded-lg border p-4">
          <h2 className="text-lg font-semibold">{reservation.businessName}</h2>

          <div className="space-y-2">
            <div className="flex items-start gap-2">
              <MapPin className="mt-0.5 h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm text-gray-600">
                {reservation.businessAddress}
              </p>
            </div>

            <div className="flex items-center gap-2">
              <Phone className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm text-gray-600">
                {reservation.businessContactPhone}
              </p>
            </div>
          </div>
        </div>

        {/* Reservation Details */}
        <div className="space-y-3 rounded-lg border p-4">
          <h3 className="font-semibold">예약 정보</h3>

          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <FileText className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm">{reservation.menuServiceName}</p>
            </div>

            <div className="flex items-center gap-2">
              <Calendar className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm">{reservation.reservationDate}</p>
            </div>

            <div className="flex items-center gap-2">
              <Clock className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm">
                {reservation.reservationTime.substring(0, 5)} (
                {reservation.reservationDuration}분)
              </p>
            </div>

            <div className="flex items-center gap-2">
              <CreditCard className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm font-medium">
                {reservation.reservationPrice.toLocaleString()}원
              </p>
            </div>
          </div>
        </div>

        {/* Customer Info */}
        <div className="space-y-3 rounded-lg border p-4">
          <h3 className="font-semibold">예약자 정보</h3>

          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <User className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm">{reservation.customerNameSnapshot}</p>
            </div>

            <div className="flex items-center gap-2">
              <Phone className="h-4 w-4 flex-shrink-0 text-gray-500" />
              <p className="text-sm">{reservation.customerPhoneSnapshot}</p>
            </div>
          </div>
        </div>

        {/* Notes */}
        {reservation.notes && (
          <div className="space-y-2 rounded-lg border p-4">
            <h3 className="font-semibold">메모</h3>
            <p className="text-sm text-gray-600">{reservation.notes}</p>
          </div>
        )}

        {/* Timestamps */}
        <div className="space-y-1 text-xs text-gray-500">
          <p>예약 생성: {new Date(reservation.createdAt).toLocaleString()}</p>
          <p>최종 수정: {new Date(reservation.updatedAt).toLocaleString()}</p>
          {reservation.cancelledAt && (
            <p>
              취소 일시: {new Date(reservation.cancelledAt).toLocaleString()}
            </p>
          )}
        </div>
      </div>

      {/* Actions */}
      {canCancel && (
        <div className="sticky bottom-0 border-t bg-white px-4 py-4">
          <Button
            variant="destructive"
            className="w-full"
            onClick={() => setConfirmOpen(true)}
            disabled={loading}
          >
            {loading ? '취소 중...' : '예약 취소'}
          </Button>
        </div>
      )}

      {/* Confirm Dialog */}
      <ConfirmDialog
        open={confirmOpen}
        onOpenChange={setConfirmOpen}
        title="예약 취소"
        description="정말로 예약을 취소하시겠습니까? 이 작업은 되돌릴 수 없습니다."
        confirmText="취소하기"
        cancelText="닫기"
        variant="destructive"
        onConfirm={handleCancel}
      />
    </div>
  );
}
