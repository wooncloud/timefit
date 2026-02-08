import type {
  ApiResponse,
  ChangePasswordRequest,
  CurrentUser,
  UpdateProfileRequest,
  UserProfile,
} from '@/types/user/profile';

/**
 * 사용자 프로필 관리 서비스
 */
class UserService {
  private apiUrl = '/api';

  /**
   * 현재 사용자 정보 조회 (GET /api/user/me)
   */
  async getCurrentUser(): Promise<ApiResponse<CurrentUser>> {
    const response = await fetch(`${this.apiUrl}/user/me`, {
      method: 'GET',
      credentials: 'include',
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(
        result.errorResponse?.message || '사용자 정보 조회에 실패했습니다.'
      );
    }

    return result;
  }

  /**
   * 사용자 프로필 조회 (GET /api/customer/profile)
   */
  async getUserProfile(): Promise<ApiResponse<UserProfile>> {
    const response = await fetch(`${this.apiUrl}/customer/profile`, {
      method: 'GET',
      credentials: 'include',
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(
        result.errorResponse?.message || '프로필 조회에 실패했습니다.'
      );
    }

    return result;
  }

  /**
   * 프로필 수정 (PUT /api/customer/profile)
   */
  async updateProfile(
    data: UpdateProfileRequest
  ): Promise<ApiResponse<UserProfile>> {
    const response = await fetch(`${this.apiUrl}/customer/profile`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(data),
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(
        result.errorResponse?.message || '프로필 수정에 실패했습니다.'
      );
    }

    return result;
  }

  /**
   * 비밀번호 변경 (PUT /api/customer/profile/password)
   */
  async changePassword(
    data: ChangePasswordRequest
  ): Promise<ApiResponse<void>> {
    const response = await fetch(`${this.apiUrl}/customer/profile/password`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(data),
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(
        result.errorResponse?.message || '비밀번호 변경에 실패했습니다.'
      );
    }

    return result;
  }
}

export const userService = new UserService();
