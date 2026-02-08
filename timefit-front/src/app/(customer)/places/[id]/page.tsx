import Link from 'next/link';
import { AlertCircle } from 'lucide-react';

import { getBusinessDetail } from '@/services/business/business-service';
import { checkWishlist } from '@/services/wishlist/wishlist-service';
import { getMenuList } from '@/services/menu/menu-service';
import { Button } from '@/components/ui/button';

import { PlaceDetailClient } from './place-detail-client';

interface PlacePageProps {
  params: Promise<{
    id: string;
  }>;
}

export default async function PlacePage({ params }: PlacePageProps) {
  const { id: businessId } = await params;

  try {
    // SSR: 업체 정보, 찜 여부, 메뉴 리스트 조회
    const [business, isWishlisted, menuList] = await Promise.all([
      getBusinessDetail(businessId),
      checkWishlist(businessId),
      getMenuList(businessId, { isActive: true }), // 활성화된 메뉴만 조회
    ]);

    return (
      <PlaceDetailClient
        business={business}
        businessId={businessId}
        initialWishlistStatus={isWishlisted}
        menuList={menuList}
      />
    );
  } catch (error) {
    console.error('업체 정보 조회 오류:', error);

    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <AlertCircle className="h-12 w-12 text-destructive" />
        <p className="mt-4 text-sm text-muted-foreground">
          {error instanceof Error
            ? error.message
            : '업체 정보를 찾을 수 없습니다.'}
        </p>
        <Link href="/" className="mt-4">
          <Button variant="outline">홈으로 돌아가기</Button>
        </Link>
      </div>
    );
  }
}
