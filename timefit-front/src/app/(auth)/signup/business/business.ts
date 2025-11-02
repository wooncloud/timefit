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

export const initialBusinessSignupForm: BusinessSignupFormData = {
  businessName: '',
  businessType: '',
  businessNumber: '',
  address: '',
  contactPhone: '',
  description: '',
};

/**
 * 사업자 번호를 123-45-67890 형식으로 포맷.
 */
export function formatBusinessNumber(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 10);

  if (digits.length <= 3) {
    return digits;
  }

  if (digits.length <= 5) {
    return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  }

  return `${digits.slice(0, 3)}-${digits.slice(3, 5)}-${digits.slice(5)}`;
}

/**
 * 전화번호를 지역번호-국번호-가입자번호 형식으로 포맷.
 */
export function formatContactPhone(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 11);

  if (digits.startsWith('02')) {
    if (digits.length <= 2) {
      return digits;
    }

    if (digits.length <= 6) {
      return `${digits.slice(0, 2)}-${digits.slice(2)}`;
    }

    if (digits.length <= 10) {
      return `${digits.slice(0, 2)}-${digits.slice(2, digits.length - 4)}-${digits.slice(-4)}`;
    }

    return `${digits.slice(0, 2)}-${digits.slice(2, 6)}-${digits.slice(6)}`;
  }

  if (digits.length <= 3) {
    return digits;
  }

  if (digits.length <= 7) {
    return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  }

  return `${digits.slice(0, 3)}-${digits.slice(3, digits.length - 4)}-${digits.slice(-4)}`;
}

export function validateBusinessSignupForm(
  formData: BusinessSignupFormData
): BusinessSignupValidationResult {
  const errors: BusinessSignupFormErrors = {};

  if (!formData.businessName.trim()) {
    errors.businessName = '회사명을 입력해주세요.';
  }

  if (!formData.businessType) {
    errors.businessType = '업종을 선택해주세요.';
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
