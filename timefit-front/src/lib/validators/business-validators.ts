import {
  BusinessSignupFormData,
  BusinessSignupFormErrors,
} from '@/types/auth/business/create-business';
import type { UpdateBusinessRequest } from '@/types/business/business-detail';
import { validateScriptInjection } from '@/lib/validators/input-validators';

interface BusinessSignupValidationResult {
  isValid: boolean;
  errors: BusinessSignupFormErrors;
}

interface BusinessProfileFormErrors {
  contactPhone?: string;
  contactEmail?: string;
}

interface BusinessProfileValidationResult {
  isValid: boolean;
  errors: BusinessProfileFormErrors;
}

const businessNumberPattern = /^(\d{3})-(\d{2})-(\d{5})$/;
const contactPhonePattern = /^(0\d{1,2})-\d{3,4}-\d{4}$/;
const emailPattern = /\S+@\S+\.\S+/;

/**
 * 업체 정보 수정 폼 유효성 검증
 * contactPhone, contactEmail, description, businessNotice 검증
 */
export function validateBusinessProfileForm(
  formData: UpdateBusinessRequest
): BusinessProfileValidationResult {
  const errors: BusinessProfileFormErrors = {};

  if (formData.contactPhone && !contactPhonePattern.test(formData.contactPhone)) {
    errors.contactPhone = '올바른 전화번호 형식으로 입력해주세요 (예: 02-1234-5678)';
  }

  if (formData.contactEmail && !emailPattern.test(formData.contactEmail)) {
    errors.contactEmail = '올바른 이메일 형식으로 입력해주세요';
  }

  if (formData.description && !validateScriptInjection(formData.description)) {
    errors.contactEmail = '허용되지 않는 문자가 포함되어 있습니다.';
  }

  if (formData.businessNotice && !validateScriptInjection(formData.businessNotice)) {
    errors.contactEmail = '허용되지 않는 문자가 포함되어 있습니다.';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}

/**
 * 초기 사업자 회원가입 폼 데이터
 */
export const initialBusinessSignupForm: BusinessSignupFormData = {
  businessName: '',
  businessTypes: [],
  businessNumber: '',
  address: '',
  contactPhone: '',
  description: '',
};

/**
 * 사업자 회원가입 폼 데이터 유효성 검증
 */
export function validateBusinessSignupForm(
  formData: BusinessSignupFormData
): BusinessSignupValidationResult {
  const errors: BusinessSignupFormErrors = {};

  if (!formData.businessName.trim()) {
    errors.businessName = '회사명을 입력해주세요.';
  }

  if (!formData.businessTypes || formData.businessTypes.length === 0) {
    errors.businessTypes = '업종을 선택해주세요.';
  }

  const businessNumber = formData.businessNumber.trim();
  if (!businessNumber) {
    errors.businessNumber = '사업자 번호를 입력해주세요.';
  } else if (!businessNumberPattern.test(businessNumber)) {
    errors.businessNumber =
      '올바른 사업자 번호 형식을 입력해주세요. (123-45-67890)';
  }

  if (!formData.address.trim()) {
    errors.address = '주소를 입력해주세요.';
  }

  const contactPhone = formData.contactPhone.trim();
  if (!contactPhone) {
    errors.contactPhone = '회사 전화번호를 입력해주세요.';
  } else if (!contactPhonePattern.test(contactPhone)) {
    errors.contactPhone = '올바른 전화번호 형식을 입력해주세요. (02-1234-5678)';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}

/**
 * 업체 연락처 유효성 검사 (일반 전화 + 휴대폰)
 * 예: 02-1234-5678, 010-1234-5678
 * 고객 휴대폰 번호 검증은 auth-validators.ts 참고
 * @returns true면 유효한 형식
 */
export function validateBusinessContactPhone(value: string): boolean {
  return contactPhonePattern.test(value);
}
