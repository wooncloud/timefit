'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { Calendar, CheckCircle, Clock } from 'lucide-react';

import type {
  CustomerReservationItem,
  CustomerReservationList,
  ReservationStatus,
} from '@/types/customer/reservation';
import { useCancelReservation } from '@/hooks/reservation/mutations/use-cancel-reservation';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';

interface BookingsClientProps {
  initialData: CustomerReservationList;
}

type TabType = 'upcoming' | 'completed' | 'cancelled';

export function BookingsClient({ initialData }: BookingsClientProps) {
  const [activeTab, setActiveTab] = useState<TabType>('upcoming');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const { cancelReservation } = useCancelReservation();

  const handleCancelClick = (reservationId: string) => {
    setSelectedId(reservationId);
    setConfirmOpen(true);
  };

  const handleConfirmCancel = async () => {
    if (!selectedId) return;

    const success = await cancelReservation(selectedId);
    if (success) {
      setConfirmOpen(false);
      setSelectedId(null);
      window.location.reload(); // SSR 데이터 재요청
    }
  };

  const getFilteredReservations = () => {
    switch (activeTab) {
      case 'upcoming':
        return initialData.reservations.filter(
          r => r.status === 'PENDING' || r.status === 'CONFIRMED'
        );
      case 'completed':
        return initialData.reservations.filter(r => r.status === 'COMPLETED');
      case 'cancelled':
        return initialData.reservations.filter(
          r => r.status === 'CANCELLED' || r.status === 'REJECTED'
        );
      default:
        return [];
    }
  };

  const filteredReservations = getFilteredReservations();

  return (
    <>
      <div className="flex flex-col bg-white">
        {/* 탭 */}
        <div className="border-b px-4">
          <div className="flex">
            {[
              { id: 'upcoming', label: '이용 예정' },
              { id: 'completed', label: '이용 완료' },
              { id: 'cancelled', label: '취소됨' },
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as TabType)}
                className={cn(
                  'flex-1 py-3 text-sm font-medium transition-colors',
                  activeTab === tab.id
                    ? 'border-b-2 border-[#3ec0c7] text-[#3ec0c7]'
                    : 'text-gray-500'
                )}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {/* 예약 리스트 */}
        <div className="flex-1 px-4 py-4">
          {filteredReservations.length > 0 ? (
            <div className="space-y-4">
              {filteredReservations.map(reservation => (
                <ReservationCard
                  key={reservation.reservationId}
                  reservation={reservation}
                  onCancel={() => handleCancelClick(reservation.reservationId)}
                />
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-16">
              <Calendar className="h-12 w-12 text-gray-300" />
              <p className="mt-4 text-gray-500">예약 내역이 없습니다</p>
            </div>
          )}
        </div>
      </div>

      <ConfirmDialog
        open={confirmOpen}
        onOpenChange={setConfirmOpen}
        title="예약 취소"
        description="정말로 예약을 취소하시겠습니까? 이 작업은 되돌릴 수 없습니다."
        confirmText="예약 취소"
        cancelText="닫기"
        variant="destructive"
        onConfirm={handleConfirmCancel}
      />
    </>
  );
}

// 예약 카드 컴포넌트
interface ReservationCardProps {
  reservation: CustomerReservationItem;
  onCancel: () => void;
}

function ReservationCard({ reservation, onCancel }: ReservationCardProps) {
  const canCancel = ['PENDING', 'CONFIRMED'].includes(reservation.status);
  const status = getStatusInfo(reservation.status);

  return (
    <div className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
      {/* 상단: 업체 정보 */}
      <div className="flex gap-3">
        {reservation.businessLogoUrl ? (
          <Image
            src={reservation.businessLogoUrl}
            alt={reservation.businessName}
            width={64}
            height={64}
            className="h-16 w-16 flex-shrink-0 rounded-xl object-cover"
          />
        ) : (
          <div className="h-16 w-16 flex-shrink-0 rounded-xl bg-gray-200" />
        )}
        <div className="flex-1">
          <div className="flex items-start justify-between">
            <div>
              <h3 className="font-semibold text-gray-900">
                {reservation.businessName}
              </h3>
              <p className="text-sm text-gray-500">{reservation.menuName}</p>
            </div>
            <span
              className={cn(
                'rounded-full px-2.5 py-1 text-xs font-medium',
                status.color
              )}
            >
              {status.label}
            </span>
          </div>
        </div>
      </div>

      {/* 날짜/시간 정보 */}
      <div className="mt-4 flex items-center gap-2 rounded-xl bg-gray-50 px-3 py-2.5">
        {getStatusIcon(reservation.status)}
        <div>
          <p className="text-xs text-gray-500">
            {reservation.status === 'COMPLETED'
              ? '이용 완료'
              : reservation.status === 'PENDING'
                ? '요청 시간'
                : '예약 일시'}
          </p>
          <p className="text-sm font-medium text-gray-900">
            {reservation.reservationDate} {reservation.reservationTime}
          </p>
        </div>
      </div>

      {/* 메모 정보 */}
      {reservation.customerMemo && (
        <div className="mt-3 rounded-xl bg-blue-50 px-3 py-2">
          <p className="text-xs text-gray-500">고객 메모</p>
          <p className="text-sm text-gray-700">{reservation.customerMemo}</p>
        </div>
      )}

      {reservation.businessNotes && (
        <div className="mt-3 rounded-xl bg-yellow-50 px-3 py-2">
          <p className="text-xs text-gray-500">업체 메시지</p>
          <p className="text-sm text-gray-700">{reservation.businessNotes}</p>
        </div>
      )}

      {/* 버튼 */}
      <div className="mt-4 flex gap-2">
        {canCancel && (
          <Button
            variant="outline"
            onClick={onCancel}
            className="flex-1 rounded-xl border-gray-200 text-sm font-medium"
          >
            예약 취소
          </Button>
        )}
        <Link
          href={`/places/${reservation.businessId}`}
          className={cn(canCancel ? 'flex-1' : 'w-full')}
        >
          <Button
            variant={reservation.status === 'CONFIRMED' ? 'default' : 'outline'}
            className={cn(
              'w-full rounded-xl text-sm font-medium',
              reservation.status === 'CONFIRMED' &&
                'bg-[#3ec0c7] text-white hover:bg-[#35adb3]',
              reservation.status !== 'CONFIRMED' && 'border-gray-200'
            )}
          >
            {reservation.status === 'CANCELLED' ||
            reservation.status === 'REJECTED'
              ? '다시 예약하기'
              : '상세보기'}
          </Button>
        </Link>
      </div>
    </div>
  );
}

function getStatusInfo(status: ReservationStatus): {
  label: string;
  color: string;
} {
  switch (status) {
    case 'CONFIRMED':
      return { label: '확정', color: 'bg-[#3ec0c7] text-white' };
    case 'PENDING':
      return { label: '대기중', color: 'bg-orange-100 text-orange-600' };
    case 'REJECTED':
      return { label: '거절됨', color: 'bg-red-100 text-red-600' };
    case 'CANCELLED':
      return { label: '취소됨', color: 'bg-red-100 text-red-600' };
    case 'COMPLETED':
      return { label: '이용완료', color: 'bg-gray-100 text-gray-600' };
    case 'NO_SHOW':
      return { label: '노쇼', color: 'bg-gray-100 text-gray-600' };
    default:
      return { label: status, color: 'bg-gray-100 text-gray-600' };
  }
}

function getStatusIcon(status: ReservationStatus) {
  switch (status) {
    case 'COMPLETED':
      return <CheckCircle className="h-5 w-5 text-gray-400" />;
    case 'PENDING':
      return <Clock className="h-5 w-5 text-orange-400" />;
    default:
      return <Calendar className="h-5 w-5 text-[#3ec0c7]" />;
  }
}
