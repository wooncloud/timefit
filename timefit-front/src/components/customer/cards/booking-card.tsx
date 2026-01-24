import Link from 'next/link';
import { Calendar, CheckCircle, Clock } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';

type BookingStatus = 'confirmed' | 'pending' | 'completed' | 'cancelled';

interface BookingCardProps {
  id: string;
  placeId: string;
  placeName: string;
  serviceName: string;
  status: BookingStatus;
  dateTime: string;
  image?: string;
}

const statusConfig = {
  confirmed: {
    label: '확정',
    color: 'bg-[#3ec0c7] text-white',
  },
  pending: {
    label: '대기중',
    color: 'bg-orange-100 text-orange-600',
  },
  completed: {
    label: '이용완료',
    color: 'bg-gray-100 text-gray-600',
  },
  cancelled: {
    label: '취소됨',
    color: 'bg-red-100 text-red-600',
  },
};

export function BookingCard({
  id,
  placeId,
  placeName,
  serviceName,
  status,
  dateTime,
}: BookingCardProps) {
  return (
    <div className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
      {/* 상단: 업체 정보 */}
      <div className="flex gap-3">
        <div className="h-16 w-16 flex-shrink-0 rounded-xl bg-gray-200" />
        <div className="flex-1">
          <div className="flex items-start justify-between">
            <div>
              <h3 className="font-semibold text-gray-900">{placeName}</h3>
              <p className="text-sm text-gray-500">{serviceName}</p>
            </div>
            <span
              className={cn(
                'rounded-full px-2.5 py-1 text-xs font-medium',
                statusConfig[status].color
              )}
            >
              {statusConfig[status].label}
            </span>
          </div>
        </div>
      </div>

      {/* 날짜/시간 정보 */}
      <div className="mt-4 flex items-center gap-2 rounded-xl bg-gray-50 px-3 py-2.5">
        {status === 'completed' ? (
          <CheckCircle className="h-5 w-5 text-gray-400" />
        ) : status === 'pending' ? (
          <Clock className="h-5 w-5 text-orange-400" />
        ) : (
          <Calendar className="h-5 w-5 text-[#3ec0c7]" />
        )}
        <div>
          <p className="text-xs text-gray-500">
            {status === 'completed'
              ? '이용 완료'
              : status === 'pending'
                ? '요청 시간'
                : '예약 일시'}
          </p>
          <p className="text-sm font-medium text-gray-900">{dateTime}</p>
        </div>
      </div>

      {/* 버튼 */}
      <div className="mt-4 flex gap-2">
        {status === 'confirmed' && (
          <>
            <Button
              variant="outline"
              className="flex-1 rounded-xl border-gray-200 text-sm font-medium"
            >
              취소하기
            </Button>
            <Link href={`/places/${placeId}`} className="flex-1">
              <Button className="w-full rounded-xl bg-[#3ec0c7] text-sm font-medium text-white hover:bg-[#35adb3]">
                상세보기
              </Button>
            </Link>
          </>
        )}

        {status === 'pending' && (
          <>
            <Button
              variant="outline"
              className="flex-1 rounded-xl border-gray-200 text-sm font-medium"
            >
              예약 취소
            </Button>
            <Link href={`/places/${placeId}`} className="flex-1">
              <Button
                variant="outline"
                className="w-full rounded-xl border-[#3ec0c7] text-sm font-medium text-[#3ec0c7]"
              >
                상세보기
              </Button>
            </Link>
          </>
        )}

        {status === 'completed' && (
          <Link href={`/places/${placeId}`} className="flex-1">
            <Button
              variant="outline"
              className="w-full rounded-xl border-gray-200 text-sm font-medium"
            >
              상세보기
            </Button>
          </Link>
        )}

        {status === 'cancelled' && (
          <Link href={`/places/${placeId}`} className="flex-1">
            <Button
              variant="outline"
              className="w-full rounded-xl border-gray-200 text-sm font-medium"
            >
              다시 예약하기
            </Button>
          </Link>
        )}
      </div>
    </div>
  );
}
