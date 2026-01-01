import { getTeamMembers } from '@/services/team/team-service';
import { getCurrentUserFromSession } from '@/lib/session/server';

import { TeamClient } from './team-client';

export default async function Page() {
  const sessionUser = await getCurrentUserFromSession();

  if (!sessionUser) {
    throw new Error('세션 사용자 정보를 찾을 수 없습니다.');
  }

  const businessId = sessionUser.businesses?.[0]?.businessId;
  const currentUserId = sessionUser.userId;

  if (!businessId) {
    throw new Error('업체 ID를 찾을 수 없습니다.');
  }

  if (!currentUserId) {
    throw new Error('사용자 ID를 찾을 수 없습니다.');
  }

  const members = await getTeamMembers(businessId);

  return (
    <TeamClient
      initialMembers={members}
      businessId={businessId}
      currentUserId={currentUserId}
    />
  );
}
