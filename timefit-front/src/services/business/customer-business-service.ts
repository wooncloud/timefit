import 'server-only';

import type {
  BusinessCustomerList,
  CustomerSortType,
  GetBusinessCustomerListApiResponse,
} from '@/types/business/customer-business';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 사업자용 고객 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getBusinessCustomers(
  businessId: string,
  params?: {
    keyword?: string;
    sortBy?: CustomerSortType;
    page?: number;
    size?: number;
  }
): Promise<BusinessCustomerList> {
  const queryParams = new URLSearchParams();
  if (params?.keyword) queryParams.append('keyword', params.keyword);
  if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
  if (params?.page !== undefined) queryParams.append('page', params.page.toString());
  if (params?.size !== undefined) queryParams.append('size', params.size.toString());

  const queryString = queryParams.toString();
  const url = `${BACKEND_URL}/api/business/${businessId}/customers${queryString ? `?${queryString}` : ''}`;

  const response = await apiFetch(url, { method: 'GET' });

  if (!response.ok) throw new Error('고객 목록을 가져오는 데 실패했습니다.');

  const result: GetBusinessCustomerListApiResponse = await response.json();
  if (!result.data) throw new Error('고객 목록 데이터를 찾을 수 없습니다.');

  return result.data;
}