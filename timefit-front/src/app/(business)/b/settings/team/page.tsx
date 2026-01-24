import { getTeamMembers } from '@/services/team/team-service';
import { getBusinessContext } from '@/lib/business/get-business-context';

import { TeamClient } from './team-client';

export default async function Page() {
  const { businessId, userId } = await getBusinessContext();

  if (!userId) {
    throw new Error('사용자 ID를 찾을 수 없습니다.');
  }

  const members = await getTeamMembers(businessId);

  return (
    <TeamClient
      initialMembers={members}
      businessId={businessId}
      currentUserId={userId}
    />
  );
}
