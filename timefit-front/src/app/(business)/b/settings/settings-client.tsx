'use client';

import { useRouter } from 'next/navigation';
import Script from 'next/script';

import type { BusinessProfileDetail } from '@/types/business/business-detail';
import { useUpdateBusiness } from '@/hooks/business/mutations/use-update-business';
import { useBusinessProfileForm } from '@/hooks/business/use-business-profile-form';
import { AddressSearch } from '@/components/business/settings/address-search';
import { BusinessTypeSelect } from '@/components/business/settings/business-type-select';
import { FormLabel } from '@/components/business/settings/form-label';
import { SettingsHeader } from '@/components/business/settings/settings-header';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';

interface SettingsClientProps {
  initialBusiness: BusinessProfileDetail;
  businessId: string;
}

export function SettingsClient({
                                 initialBusiness,
                                 businessId,
                               }: SettingsClientProps) {
  const router = useRouter();
  const { updateBusiness, updating } = useUpdateBusiness(businessId);
  const {
    currentValues, hasChanges,
    phoneError, emailError,
    handleChange, handleAddressSearch, handleSave,
  } = useBusinessProfileForm({
    initialBusiness, updateBusiness, onSaveSuccess: () => router.refresh(),
  });

  const isDisabled = updating || !hasChanges;

  return (
    <>
      {/* 카카오 우편번호 스크립트 */}
      <Script
        src="//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"
        strategy="lazyOnload"
      />

      <div>
        <Card>
          <SettingsHeader onSave={handleSave} disabled={isDisabled} />
          <CardContent className="space-y-6">
            <div className="grid grid-cols-2 gap-6">
              <div className="space-y-2">
                <FormLabel text="상호명" required />
                <Input
                  name="businessName"
                  value={currentValues.businessName}
                  placeholder="홍길동의 카페"
                  onChange={e => handleChange('businessName', e.target.value)}
                  disabled={updating}
                />
              </div>
              <div className="space-y-2">
                <FormLabel text="업종" required />
                <BusinessTypeSelect
                  value={currentValues.businessTypes[0] ?? ''}
                  onValueChange={value => handleChange('businessTypes', [value])}
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-6">
              <div className="space-y-2">
                <FormLabel text="사업자등록번호" required />
                <Input
                  value={initialBusiness.businessNumber}
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
                  value={initialBusiness.ownerName ?? ''}
                  placeholder="홍길동"
                  disabled
                />
              </div>
            </div>

            <div className="space-y-2">
              <FormLabel text="주소" required />
              <AddressSearch
                value={currentValues.address}
                onChange={value => handleChange('address', value)}
                onSearch={handleAddressSearch}
              />
            </div>

            <div className="grid grid-cols-2 gap-6">
              <div className="space-y-2">
                <FormLabel text="연락처" required />
                <Input
                  placeholder="02-1234-5678"
                  value={currentValues.contactPhone}
                  onChange={e => handleChange('contactPhone', e.target.value)}
                  disabled={updating}
                />
                {phoneError && (
                  <p className="text-xs text-destructive">{phoneError}</p>
                )}
              </div>
              <div className="space-y-2">
                <FormLabel text="이메일" />
                <Input
                  type="email"
                  placeholder="cafe@example.com"
                  value={currentValues.contactEmail}
                  onChange={e => handleChange('contactEmail', e.target.value)}
                  disabled={updating}
                />
                {emailError && (
                  <p className="text-xs text-destructive">{emailError}</p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <FormLabel text="업체 소개" />
              <Textarea
                placeholder="맛있는 커피와 디저트를 제공하는 아늑한 카페입니다."
                className="min-h-[120px]"
                value={currentValues.description}
                onChange={e => handleChange('description', e.target.value)}
                disabled={updating}
              />
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  );
}