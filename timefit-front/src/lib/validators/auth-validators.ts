import { SigninFormData, SigninFormErrors } from '@/types/auth/signin';
import { SignupFormData, SignupFormErrors } from '@/types/auth/signup';

interface SignupValidationResult {
  isValid: boolean;
  errors: SignupFormErrors;
}

interface SigninValidationResult {
  isValid: boolean;
  errors: SigninFormErrors;
}

const emailPattern = /\S+@\S+\.\S+/;
const phoneNumberPattern = /^010-\d{4}-\d{4}$/;
const passwordPattern =
  /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]+$/;

/**
 * 비밀번호 유효성 검증 결과
 */
export interface PasswordValidationResult {
  isValid: boolean;
  errors: string[];
}

/**
 * 비밀번호 유효성 검증
 * - 최소 8자 이상
 * - 영문, 숫자, 특수문자 포함
 */
export function validatePassword(password: string): PasswordValidationResult {
  const errors: string[] = [];

  if (password.length < 8) {
    errors.push('비밀번호는 최소 8자 이상이어야 합니다.');
  }

  if (!passwordPattern.test(password)) {
    errors.push('비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.');
  }

  return {
    isValid: errors.length === 0,
    errors,
  };
}

/**
 * 비밀번호 일치 확인
 */
export function validatePasswordMatch(
  password: string,
  confirmPassword: string
): { isValid: boolean; error?: string } {
  if (password !== confirmPassword) {
    return {
      isValid: false,
      error: '새 비밀번호가 일치하지 않습니다.',
    };
  }
  return { isValid: true };
}

/**
 * 회원가입 폼 데이터 유효성 검증
 */
export function validateSignupForm(
  formData: SignupFormData
): SignupValidationResult {
  const errors: SignupFormErrors = {};

  if (!formData.email) {
    errors.email = '이메일을 입력해주세요.';
  } else if (!emailPattern.test(formData.email)) {
    errors.email = '올바른 이메일 형식을 입력해주세요.';
  }

  if (!formData.password) {
    errors.password = '비밀번호를 입력해주세요.';
  } else if (formData.password.length < 8) {
    errors.password = '비밀번호는 8자 이상이어야 합니다.';
  }

  if (!formData.confirmPassword) {
    errors.confirmPassword = '비밀번호 확인을 입력해주세요.';
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = '비밀번호가 일치하지 않습니다.';
  }

  if (!formData.name) {
    errors.name = '이름을 입력해주세요.';
  }

  if (!formData.phoneNumber) {
    errors.phoneNumber = '전화번호를 입력해주세요.';
  } else if (!phoneNumberPattern.test(formData.phoneNumber)) {
    errors.phoneNumber = '올바른 전화번호 형식을 입력해주세요. (010-1234-5678)';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}

/**
 * 로그인 폼 데이터 유효성 검증
 */
export function validateSigninForm(
  formData: SigninFormData
): SigninValidationResult {
  const errors: SigninFormErrors = {};

  if (!formData.email) {
    errors.email = '이메일을 입력해주세요.';
  } else if (!emailPattern.test(formData.email)) {
    errors.email = '올바른 이메일 형식을 입력해주세요.';
  }

  if (!formData.password) {
    errors.password = '비밀번호를 입력해주세요.';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}
