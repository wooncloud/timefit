import { useMemo, useState } from 'react';

import type {
  BusinessProfileDetail,
  UpdateBusinessRequest,
} from '@/types/business/business-detail';
import { validateBusinessProfileForm } from '@/lib/validators/business-validators';
import { validateScriptInjection } from '@/lib/validators/input-validators';

interface UseBusinessProfileFormProps {
  initialBusiness: BusinessProfileDetail;
  onSaveSuccess: () => void;
  updateBusiness: (data: UpdateBusinessRequest) => Promise<boolean>;
}

export function useBusinessProfileForm({
                                         initialBusiness,
                                         onSaveSuccess,
                                         updateBusiness,
                                       }: UseBusinessProfileFormProps) {
  const [formData, setFormData] = useState<UpdateBusinessRequest>({});
  const [phoneError, setPhoneError] = useState('');
  const [emailError, setEmailError] = useState('');

  // 현재 각 필드 값 (formData 우선, 없으면 initialBusiness)
  const currentValues = useMemo(() => ({
    businessName: formData.businessName ?? initialBusiness.businessName,
    businessTypes: formData.businessTypes ?? initialBusiness.businessTypes,
    address: formData.address ?? initialBusiness.address,
    contactPhone: formData.contactPhone ?? initialBusiness.contactPhone,
    contactEmail: formData.contactEmail ?? (initialBusiness.contactEmail ?? ''),
    description: formData.description ?? initialBusiness.description,
  }), [formData, initialBusiness]);

  // 변경사항 감지 — formData에 하나라도 있으면 변경된 것
  const hasChanges = Object.keys(formData).length > 0;

  // 폼 입력 핸들러
  const handleChange = (
    field: keyof UpdateBusinessRequest,
    value: string | string[]
  ) => {
    // 스크립트 차단 (description, businessNotice만 적용)
    if (
      (field === 'description' || field === 'businessNotice') &&
      typeof value === 'string' &&
      !validateScriptInjection(value)
    ) {
      return;
    }
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  // 카카오 주소 검색
  const handleAddressSearch = () => {
    new (window as any).kakao.Postcode({
      oncomplete: (data: any) => {
        const addr =
          data.userSelectedType === 'R'
            ? data.roadAddress
            : data.jibunAddress;
        handleChange('address', addr);
      },
    }).open();
  };

  // 저장 핸들러
  const handleSave = async () => {
    const { isValid, errors } = validateBusinessProfileForm(formData);
    setPhoneError(errors.contactPhone ?? '');
    setEmailError(errors.contactEmail ?? '');
    if (!isValid) return;

    const success = await updateBusiness(formData);
    if (success) {
      setFormData({});
      onSaveSuccess();
    }
  };

  return {
    currentValues,
    hasChanges,
    phoneError,
    emailError,
    handleChange,
    handleAddressSearch,
    handleSave,
  };
}