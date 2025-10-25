'use client';

import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { SettingsHeader } from './components/settings-header';
import { FormLabel } from './components/form-label';
import { BusinessTypeSelect } from './components/business-type-select';
import { AddressSearch } from './components/address-search';

export default function Page() {
  return (
    <div>
      <Card>
        <SettingsHeader title="기본 정보" />
        <CardContent className="space-y-6">
          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="상호명" required />
              <Input placeholder="홍길동의 카페" />
            </div>
            <div className="space-y-2">
              <FormLabel text="업종" required />
              <BusinessTypeSelect />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="사업자등록번호" required />
              <Input placeholder="123-45-67890" />
              <p className="text-xs text-muted-foreground">
                사업자등록번호는 변경할 수 없습니다
              </p>
            </div>
            <div className="space-y-2">
              <FormLabel text="대표자명" />
              <Input placeholder="홍길동" />
            </div>
          </div>

          <div className="space-y-2">
            <FormLabel text="주소" required />
            <AddressSearch />
          </div>

          <div className="grid grid-cols-2 gap-6">
            <div className="space-y-2">
              <FormLabel text="연락처" required />
              <Input placeholder="02-1234-5678" />
            </div>
            <div className="space-y-2">
              <FormLabel text="이메일" />
              <Input type="email" placeholder="cafe@example.com" />
            </div>
          </div>

          <div className="space-y-2">
            <FormLabel text="업체 소개" />
            <Textarea
              placeholder="맛있는 커피와 디저트를 제공하는 아늑한 카페입니다. 신선한 원두와 수제 디저트로 고객님께 특별한 경험을 선사합니다."
              className="min-h-[120px]"
            />
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
