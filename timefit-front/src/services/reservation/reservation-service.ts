import 'server-only';

import type {
  CustomerReservationList,
  GetReservationsApiResponse,
} from '@/types/customer/reservation';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 예약 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getReservations(
  status?: string,
  page: number = 0,
  size: number = 20
): Promise<CustomerReservationList> {
  let url = `${BACKEND_URL}/api/reservations?page=${page}&size=${size}`;
  if (status) {
    url += `&status=${status}`;
  }

  const response = await apiFetch(url, { method: 'GET' });

  if (!response.ok) {
    throw new Error('예약 목록을 가져오는 데 실패했습니다.');
  }

  const result: GetReservationsApiResponse = await response.json();

  if (!result.data) {
    throw new Error('예약 목록 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
