import type {
  SignupRequestBody,
  SignupHandlerResponse,
} from '@/types/auth/signup';

class AuthService {
  private apiUrl = '/api/auth';

  /**
   * 회원가입 API 호출
   */
  async signup(data: SignupRequestBody): Promise<SignupHandlerResponse> {
    const response = await fetch(`${this.apiUrl}/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    const result = (await response.json()) as SignupHandlerResponse;

    if (!response.ok) {
      throw new Error(result.message || '회원가입에 실패했습니다.');
    }

    return result;
  }
}

export const authService = new AuthService();
