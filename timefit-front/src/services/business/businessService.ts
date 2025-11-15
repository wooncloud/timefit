import type {
  CreateBusinessRequestBody,
  CreateBusinessHandlerResponse,
} from '@/types/auth/business/createBusiness';

class BusinessService {
  private apiUrl = '/api/auth/business';
  private businessApiUrl = '/api/business';

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
}

export const businessService = new BusinessService();
