import {
  BusinessSignupFormData,
  BusinessSignupFormErrors,
} from '@/types/auth/business/createBusiness';

interface BusinessSignupValidationResult {
  isValid: boolean;
  errors: BusinessSignupFormErrors;
}

const businessNumberPattern = /^(\d{3})-(\d{2})-(\d{5})$/;
const contactPhonePattern = /^(0\d{1,2})-\d{3,4}-\d{4}$/;

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
