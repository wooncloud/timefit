import 'server-only';

import type {
  GetTeamMembersApiResponse,
  MemberListResponse,
} from '@/types/business/team-member';
import { apiFetch } from '@/lib/api/api-fetch';

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL;

/**
 * 서버 측 함수: 팀 구성원 목록 조회
 * SSR을 위한 서버 컴포넌트에서 사용됨
 */
export async function getTeamMembers(
  businessId: string
): Promise<MemberListResponse> {
  const response = await apiFetch(
    `${BACKEND_URL}/api/business/${businessId}/members`,
    { method: 'GET' }
  );

  if (!response.ok) {
    throw new Error('팀 구성원을 가져오는 데 실패했습니다.');
  }

  const result: GetTeamMembersApiResponse = await response.json();

  if (!result.data) {
    throw new Error('팀 구성원 데이터를 찾을 수 없습니다.');
  }

  return result.data;
}
