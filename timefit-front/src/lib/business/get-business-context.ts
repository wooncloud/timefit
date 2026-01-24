import { getCurrentUserFromSession } from '@/lib/session/server';

/**
 * 현재 세션에서 비즈니스 컨텍스트 정보를 추출합니다.
 * Server Component에서만 사용 가능합니다.
 */
export async function getBusinessContext() {
  const sessionUser = await getCurrentUserFromSession();

  if (!sessionUser) {
    throw new Error('세션 사용자 정보를 찾을 수 없습니다.');
  }

  const businessId = sessionUser.businesses?.[0]?.businessId;
  const businessType = sessionUser.businesses?.[0]?.businessTypes?.[0];
  const userId = sessionUser.userId;

  if (!businessId) {
    throw new Error('업체 ID를 찾을 수 없습니다.');
  }

  return {
    sessionUser,
    businessId,
    businessType,
    userId,
  };
}

/**
 * 비즈니스 ID만 필요한 경우 사용하는 간편 함수
 */
export async function getBusinessId() {
  const { businessId } = await getBusinessContext();
  return businessId;
}

/**
 * 비즈니스 타입이 필요한 경우 사용하는 함수
 */
export async function getBusinessContextWithType() {
  const context = await getBusinessContext();

  if (!context.businessType) {
    throw new Error('업체 타입 정보를 찾을 수 없습니다.');
  }

  return context as typeof context & { businessType: string };
}
