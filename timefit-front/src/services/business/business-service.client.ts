import type {
  CreateBusinessHandlerResponse,
  CreateBusinessRequestBody,
} from '@/types/auth/business/create-business';
import type {
  ApiResponse,
  BusinessListResponse,
  BusinessSearchParams,
  PublicBusinessDetail,
} from '@/types/business/business';

class BusinessService {
  private apiUrl = '/api/auth/business';

  /**
   * 사업자 등록 API 호출
   */
  async createBusiness(
    data: CreateBusinessRequestBody
  ): Promise<CreateBusinessHandlerResponse> {
    const response = await fetch(this.apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    const result = (await response.json()) as CreateBusinessHandlerResponse;

    if (!response.ok) {
      throw new Error(result.message || '사업자 등록에 실패했습니다.');
    }

    return result;
  }

  /**
   * 업체 검색 (GET /api/business/search)
   */
  async searchBusinesses(
    params: BusinessSearchParams = {}
  ): Promise<ApiResponse<BusinessListResponse>> {
    const searchParams = new URLSearchParams();

    if (params.keyword) searchParams.append('keyword', params.keyword);
    if (params.businessType)
      searchParams.append('businessType', params.businessType);
    if (params.region) searchParams.append('region', params.region);
    if (params.page !== undefined)
      searchParams.append('page', params.page.toString());
    if (params.size !== undefined)
      searchParams.append('size', params.size.toString());

    const queryString = searchParams.toString();
    const url = `/api/business/search${queryString ? `?${queryString}` : ''}`;

    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include',
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(
        result.errorResponse?.message || '업체 검색에 실패했습니다.'
      );
    }

    return result;
  }

  /**
   * 업체 상세 조회 (GET /api/business/:businessId)
   */
  async getBusinessDetail(
    businessId: string
  ): Promise<ApiResponse<PublicBusinessDetail>> {
    const url = `/api/business/${businessId}`;

    const response = await fetch(url, {
      method: 'GET',
      credentials: 'include',
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(
        result.errorResponse?.message || '업체 정보를 불러오는데 실패했습니다.'
      );
    }

    return result;
  }
}

export const businessService = new BusinessService();
