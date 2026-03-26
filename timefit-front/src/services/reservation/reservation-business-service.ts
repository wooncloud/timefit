import 'server-only';

import type {
    BusinessReservationList,
    GetBusinessReservationListApiResponse,
} from '@/types/business/reservation';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 사업자용 예약 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getBusinessReservations(
    businessId: string,
    params?: {
        status?: string;
        startDate?: string;
        endDate?: string;
        page?: number;
        size?: number;
    }
): Promise<BusinessReservationList> {
    const queryParams = new URLSearchParams();
    if (params?.status) queryParams.append('status', params.status);
    if (params?.startDate) queryParams.append('startDate', params.startDate);
    if (params?.endDate) queryParams.append('endDate', params.endDate);
    if (params?.page !== undefined)
        queryParams.append('page', params.page.toString());
    if (params?.size !== undefined)
        queryParams.append('size', params.size.toString());

    const queryString = queryParams.toString();
    const url = `${BACKEND_URL}/api/business/${businessId}/reservations${queryString ? `?${queryString}` : ''}`;

    const response = await apiFetch(url, { method: 'GET' });

    if (!response.ok) {
        throw new Error('예약 목록을 가져오는 데 실패했습니다.');
    }

    const result: GetBusinessReservationListApiResponse = await response.json();

    if (!result.data) {
        throw new Error('예약 목록 데이터를 찾을 수 없습니다.');
    }

    return result.data;
}