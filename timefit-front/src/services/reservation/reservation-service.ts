import 'server-only';

import type {
  CustomerReservation,
  CustomerReservationList,
  GetCustomerReservationDetailApiResponse,
  GetCustomerReservationListApiResponse,
} from '@/types/customer/reservation';
import { getServerSession } from '@/lib/session/server';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

if (!BACKEND_URL) {
  throw new Error('NEXT_PUBLIC_BACKEND_URL이 정의되지 않았습니다.');
}

/**
 * 서버 측 함수: 내 예약 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getMyReservations(params?: {
  status?: string;
  startDate?: string;
  endDate?: string;
  businessId?: string;
  page?: number;
  size?: number;
}): Promise<CustomerReservationList> {
  const session = await getServerSession();

  const queryParams = new URLSearchParams();
  if (params?.status) queryParams.append('status', params.status);
  if (params?.startDate) queryParams.append('startDate', params.startDate);
  if (params?.endDate) queryParams.append('endDate', params.endDate);
  if (params?.businessId) queryParams.append('businessId', params.businessId);
  if (params?.page !== undefined)
    queryParams.append('page', params.page.toString());
  if (params?.size !== undefined)
    queryParams.append('size', params.size.toString());

  const queryString = queryParams.toString();
  const url = `${BACKEND_URL}/api/reservations${queryString ? `?${queryString}` : ''}`;

  const response = await fetch(url, {
    headers: {
      Authorization: `Bearer ${session.user?.accessToken}`,
      'Content-Type': 'application/json',
    },
    cache: 'no-store',
  });

  if (!response.ok) {
    throw new Error('예약 목록을 가져오는 데 실패했습니다.');
  }

  const result: GetCustomerReservationListApiResponse = await response.json();

  if (!result.data) {
    throw new Error('예약 목록 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}

/**
 * 서버 측 함수: 예약 상세 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getReservationDetail(
  reservationId: string
): Promise<CustomerReservation> {
  const session = await getServerSession();

  const response = await fetch(
    `${BACKEND_URL}/api/reservation/${reservationId}`,
    {
      headers: {
        Authorization: `Bearer ${session.user?.accessToken}`,
        'Content-Type': 'application/json',
      },
      cache: 'no-store',
    }
  );

  if (!response.ok) {
    throw new Error('예약 상세 정보를 가져오는 데 실패했습니다.');
  }

  const result: GetCustomerReservationDetailApiResponse = await response.json();

  if (!result.data) {
    throw new Error('예약 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
