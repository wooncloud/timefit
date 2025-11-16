'use client';

import { useState } from 'react';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { SettingsHeader } from '@/components/business/settings/settings-header';
import { FormLabel } from '@/components/business/settings/form-label';
import { BusinessTypeSelect } from '@/components/business/settings/business-type-select';
import { AddressSearch } from '@/components/business/settings/address-search';
import { useBusinessStore } from '@/store';
import { useBusinessDetail } from '@/hooks/business/useBusinessDetail';
import type { UpdateBusinessRequest } from '@/types/business/businessDetail';
import { toast } from 'sonner';

export default function Page() {
  const { business: storedBusiness } = useBusinessStore();
  const {
    business: detailBusiness,
    loading,
    error,
    updateBusiness,
    updating,
  } = useBusinessDetail(storedBusiness?.businessId || '');

  // 폼 상태 관리
  const [formData, setFormData] = useState<UpdateBusinessRequest>({});

  if (loading) {
    return <div>로딩 중...</div>;
  }

  if (error) {
    return <div>오류: {error}</div>;
  }

  if (!detailBusiness) {
    return <div>사업자 정보가 없습니다.</div>;
  }

  const business = detailBusiness;

  // 폼 입력 핸들러
  const handleChange = (
    field: keyof UpdateBusinessRequest,
    value: string | string[]
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  // 저장 핸들러
  const handleSave = async () => {
    const success = await updateBusiness(formData);
    if (success) {
      toast('업체 정보가 수정되었습니다.');
      setFormData({}); // 폼 초기화
    }
  };

  return (
    <div>
      <Card>
        <SettingsHeader onSave={handleSave} />
        <CardContent className="space-y-6">
          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="상호명" required />
              <Input
                name="businessName"
                value={formData.businessName ?? business.businessName}
                placeholder="홍길동의 카페"
                onChange={(e) => handleChange('businessName', e.target.value)}
                disabled={updating}
              />
            </div>
            <div className="space-y-2">
              <FormLabel text="업종" required />
              <BusinessTypeSelect
                value={
                  formData.businessTypes?.[0] ?? (business.businessTypes[0] || '')
                }
                onValueChange={(value) =>
                  handleChange('businessTypes', [value])
                }
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="사업자등록번호" required />
              <Input placeholder="123-45-67890" disabled />
              <p className="text-xs text-muted-foreground">
                사업자등록번호는 변경할 수 없습니다
              </p>
            </div>
            <div className="space-y-2">
              <FormLabel text="대표자명" />
              <Input placeholder="홍길동" disabled />
            </div>
          </div>

          <div className="space-y-2">
            <FormLabel text="주소" required />
            <AddressSearch
              value={formData.address ?? business.address}
              onChange={(value) => handleChange('address', value)}
            />
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="연락처" required />
              <Input
                placeholder="02-1234-5678"
                value={formData.contactPhone ?? business.contactPhone}
                onChange={(e) => handleChange('contactPhone', e.target.value)}
                disabled={updating}
              />
            </div>
            <div className="space-y-2">
              <FormLabel text="이메일" />
              <Input
                type="email"
                placeholder="cafe@example.com"
                disabled={updating}
              />
            </div>
          </div>

          <div className="space-y-2">
            <FormLabel text="업체 소개" />
            <Textarea
              placeholder="맛있는 커피와 디저트를 제공하는 아늑한 카페입니다. 신선한 원두와 수제 디저트로 고객님께 특별한 경험을 선사합니다."
              className="min-h-[120px]"
              value={formData.description ?? business.description}
              onChange={(e) => handleChange('description', e.target.value)}
              disabled={updating}
            />
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
