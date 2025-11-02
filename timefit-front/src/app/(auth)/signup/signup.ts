import { SignupFormData, SignupFormErrors } from '@/types/auth/signup';

interface SignupValidationResult {
  isValid: boolean;
  errors: SignupFormErrors;
}

const emailPattern = /\S+@\S+\.\S+/;
const phoneNumberPattern = /^010-\d{4}-\d{4}$/;

/**
 * 숫자만 입력된 전화번호 문자열에 010-XXXX-XXXX 형식을 적용.
 */
export function formatPhoneNumber(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 11);

  if (digits.length <= 3) {
    return digits;
  }

  if (digits.length <= 7) {
    return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  }

  return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
}

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
