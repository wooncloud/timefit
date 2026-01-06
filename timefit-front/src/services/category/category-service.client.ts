import type {
  CreateCategoryHandlerResponse,
  CreateCategoryRequest,
  DeleteCategoryHandlerResponse,
  UpdateCategoryHandlerResponse,
  UpdateCategoryRequest,
} from '@/types/category/category';

/**
 * 클라이언트 측 클래스: 카테고리 변경 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class CategoryService {
  /**
   * 카테고리 생성 (API 라우트를 통한 클라이언트 측 호출)
   */
  async createCategory(
    businessId: string,
    data: CreateCategoryRequest
  ): Promise<CreateCategoryHandlerResponse> {
    const response = await fetch(`/api/business/${businessId}/category`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return response.json();
  }

  /**
   * 카테고리 수정 (API 라우트를 통한 클라이언트 측 호출)
   */
  async updateCategory(
    businessId: string,
    categoryId: string,
    data: UpdateCategoryRequest
  ): Promise<UpdateCategoryHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/category/${categoryId}`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      }
    );

    return response.json();
  }

  /**
   * 카테고리 삭제 (API 라우트를 통한 클라이언트 측 호출)
   */
  async deleteCategory(
    businessId: string,
    categoryId: string
  ): Promise<DeleteCategoryHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/category/${categoryId}`,
      {
        method: 'DELETE',
      }
    );

    return response.json();
  }
}

export const categoryService = new CategoryService();
