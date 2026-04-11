import type {
  GetBusinessCustomerDetailHandlerResponse,
  GetBusinessCustomerListHandlerResponse,
  UpsertCustomerMemoHandlerResponse,
  CustomerSortType,
} from '@/types/business/customer-business';

/**
 * 클라이언트 측 클래스: 사업자용 고객 액션
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class BusinessCustomerService {
  /**
   * 고객 상세 조회
   */
  async getCustomerDetail(
    businessId: string,
    customerId: string
  ): Promise<GetBusinessCustomerDetailHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/customers/${customerId}`,
      { method: 'GET' }
    );
    return response.json();
  }

  /**
   * 고객 메모 저장/수정 (null이면 메모 삭제)
   */
  async upsertCustomerMemo(
    businessId: string,
    customerId: string,
    memo: string | null
  ): Promise<UpsertCustomerMemoHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/customers/${customerId}/memo`,
      {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ memo }),
      }
    );
    return response.json();
  }

  async getCustomers(
    businessId: string,
    params: {
      keyword?: string;
      sortBy?: CustomerSortType;
      page?: number;
      size?: number;
    }
  ): Promise<GetBusinessCustomerListHandlerResponse> {
    const searchParams = new URLSearchParams();
    if (params.keyword) searchParams.append('keyword', params.keyword);
    if (params.sortBy) searchParams.append('sortBy', params.sortBy);
    if (params.page !== undefined) searchParams.append('page', params.page.toString());
    if (params.size !== undefined) searchParams.append('size', params.size.toString());

    const response = await fetch(
      `/api/business/${businessId}/customers?${searchParams.toString()}`,
      { method: 'GET' }
    );
    return response.json();
  }
}

export const businessCustomerService = new BusinessCustomerService();