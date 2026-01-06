import type {
  UpdateBusinessHandlerResponse,
  UpdateBusinessRequest,
} from '@/types/business/business-detail';

/**
 * 클라이언트 측 클래스: 비즈니스 정보 변경 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class BusinessDetailService {
  /**
   * 비즈니스 프로필 수정 (API 라우트를 통한 클라이언트 측 호출)
   */
  async updateBusiness(
    businessId: string,
    data: UpdateBusinessRequest
  ): Promise<UpdateBusinessHandlerResponse> {
    const response = await fetch(`/api/business/${businessId}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return response.json();
  }
}

export const businessDetailService = new BusinessDetailService();
