import Link from 'next/link';
import { AlertCircle } from 'lucide-react';

import { getBusinessDetail } from '@/services/business/business-service';
import { getMenuList } from '@/services/menu/menu-service';
import { getCurrentUserFromSession } from '@/lib/session/server';
import { Button } from '@/components/ui/button';

import { ReserveClient } from './reserve-client';

interface ReservePageProps {
  params: Promise<{
    id: string;
  }>;
  searchParams: Promise<{
    menuIds?: string;
  }>;
}

export default async function ReservePage({
  params,
  searchParams,
}: ReservePageProps) {
  const { id: businessId } = await params;
  const { menuIds } = await searchParams;

  try {
    // SSR: 업체 정보, 메뉴 리스트, 세션 사용자 조회
    const [business, menuList, sessionUser] = await Promise.all([
      getBusinessDetail(businessId),
      getMenuList(businessId, { isActive: true }),
      getCurrentUserFromSession(),
    ]);

    if (!menuIds) {
      return (
        <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
          <AlertCircle className="h-12 w-12 text-destructive" />
          <p className="mt-4 text-center text-gray-600">
            선택한 서비스가 없습니다.
          </p>
          <Link href={`/places/${businessId}`} className="mt-4">
            <Button variant="outline">뒤로 가기</Button>
          </Link>
        </div>
      );
    }

    // menuIds를 배열로 변환
    const selectedMenuIds = menuIds.split(',');

    // 선택한 메뉴들만 필터링
    const selectedMenus = menuList.menus.filter(menu =>
      selectedMenuIds.includes(menu.menuId)
    );

    return (
      <ReserveClient
        business={business}
        businessId={businessId}
        selectedMenus={selectedMenus}
        sessionUser={sessionUser}
      />
    );
  } catch (error) {
    console.error('예약 페이지 로드 오류:', error);

    return (
      <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
        <AlertCircle className="h-12 w-12 text-destructive" />
        <p className="mt-4 text-center text-gray-600">
          {error instanceof Error
            ? error.message
            : '업체 정보를 불러오는 데 실패했습니다.'}
        </p>
        <Link href="/" className="mt-4">
          <Button variant="outline">홈으로 돌아가기</Button>
        </Link>
      </div>
    );
  }
}
