/**
 * 카테고리 정보
 * 백엔드 CategoryResponse.CategoryInfo에 대응
 */
export interface Category {
  categoryId: string;
  businessId: string;
  businessType: string;
  categoryName: string;
  categoryNotice: string | null;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * 카테고리 생성 요청
 * 백엔드 CategoryRequest.CreateCategory에 대응
 */
export interface CreateCategoryRequest {
  businessType: string;
  categoryName: string;
  categoryNotice: string;
}

/**
 * 카테고리 수정 요청
 * 백엔드 CategoryRequest.UpdateCategory에 대응
 */
export interface UpdateCategoryRequest {
  categoryName: string;
  categoryNotice: string;
  isActive: boolean;
}

/**
 * 카테고리 목록 응답
 */
export interface CategoryListResponse {
  categories: Category[];
  totalCount: number;
}

/**
 * 백엔드 API 응답 - 목록 조회
 */
export interface GetCategoryListApiResponse {
  data?: CategoryListResponse;
  message?: string;
}

/**
 * 백엔드 API 응답 - 상세 조회
 */
export interface GetCategoryDetailApiResponse {
  data?: Category;
  message?: string;
}

/**
 * 백엔드 API 응답 - 생성
 */
export interface CreateCategoryApiResponse {
  data?: Category;
  message?: string;
}

/**
 * 백엔드 API 응답 - 수정
 */
export interface UpdateCategoryApiResponse {
  data?: Category;
  message?: string;
}

/**
 * Next.js API 라우트 응답 - 목록 조회
 */
export interface GetCategoryListHandlerResponse {
  success: boolean;
  data?: CategoryListResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * Next.js API 라우트 응답 - 상세 조회
 */
export interface GetCategoryDetailHandlerResponse {
  success: boolean;
  data?: Category;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * Next.js API 라우트 응답 - 생성
 */
export interface CreateCategoryHandlerResponse {
  success: boolean;
  data?: Category;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * Next.js API 라우트 응답 - 수정
 */
export interface UpdateCategoryHandlerResponse {
  success: boolean;
  data?: Category;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * Next.js API 라우트 응답 - 삭제
 */
export interface DeleteCategoryHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
