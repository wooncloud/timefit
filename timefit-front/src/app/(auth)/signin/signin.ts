import { SigninFormData, SigninFormErrors } from '@/types/auth/signin';

interface SigninValidationResult {
  isValid: boolean;
  errors: SigninFormErrors;
}

const emailPattern = /\S+@\S+\.\S+/;

export const initialSigninForm: SigninFormData = {
  email: '',
  password: '',
};

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
