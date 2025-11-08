'use client';

import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { SettingsHeader } from '@/components/business/settings/settings-header';
import { FormLabel } from '@/components/business/settings/form-label';
import { BusinessTypeSelect } from '@/components/business/settings/business-type-select';
import { AddressSearch } from '@/components/business/settings/address-search';
import { useBusinessSettings } from '@/hooks/business/useBusinessSettings';

export default function Page() {
  const {
    loading,
    formData,
    handleInputChange,
    handleBusinessTypeChange,
    handleAddressChange,
  } = useBusinessSettings();

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div>
      <Card>
        <SettingsHeader />
        <CardContent className="space-y-6">
          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="상호명" required />
              <Input
                name="businessName"
                value={formData.businessName}
                onChange={handleInputChange}
                placeholder="홍길동의 카페"
              />
            </div>
            <div className="space-y-2">
              <FormLabel text="업종" required />
              <BusinessTypeSelect
                value={formData.businessTypes[0] || ''}
                onValueChange={handleBusinessTypeChange}
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="사업자등록번호" required />
              <Input
                name="businessNumber"
                value={formData.businessNumber}
                onChange={handleInputChange}
                placeholder="123-45-67890"
                disabled
              />
              <p className="text-xs text-muted-foreground">
                사업자등록번호는 변경할 수 없습니다
              </p>
            </div>
            <div className="space-y-2">
              <FormLabel text="대표자명" />
              <Input
                name="representativeName"
                value={formData.representativeName}
                onChange={handleInputChange}
                placeholder="홍길동"
              />
            </div>
          </div>

          <div className="space-y-2">
            <FormLabel text="주소" required />
            <AddressSearch
              value={formData.address}
              onChange={handleAddressChange}
            />
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="연락처" required />
              <Input
                name="contactPhone"
                value={formData.contactPhone}
                onChange={handleInputChange}
                placeholder="02-1234-5678"
              />
            </div>
            <div className="space-y-2">
              <FormLabel text="이메일" />
              <Input
                name="email"
                type="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="cafe@example.com"
              />
            </div>
          </div>

          <div className="space-y-2">
            <FormLabel text="업체 소개" />
            <Textarea
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="맛있는 커피와 디저트를 제공하는 아늑한 카페입니다. 신선한 원두와 수제 디저트로 고객님께 특별한 경험을 선사합니다."
              className="min-h-[120px]"
            />
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
