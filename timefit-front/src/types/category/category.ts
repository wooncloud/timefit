export interface Category {
  id: string;
  name: string;
  notice: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  notice: string;
  isActive: boolean;
}

export type UpdateCategoryRequest = CreateCategoryRequest;

export interface CategoryListResponse {
  categories: Category[];
  totalCount: number;
}

export interface GetCategoryListHandlerResponse {
  success: boolean;
  data?: CategoryListResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface CreateCategoryHandlerResponse {
  success: boolean;
  data?: Category;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface UpdateCategoryHandlerResponse {
  success: boolean;
  data?: Category;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

export interface DeleteCategoryHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
