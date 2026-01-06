import type {
  BusinessRole,
  ChangeMemberRoleHandlerResponse,
  ChangeMemberRoleRequest,
  DeleteMemberHandlerResponse,
  InviteMemberHandlerResponse,
  InviteMemberRequest,
  MemberStatusChangeHandlerResponse,
} from '@/types/business/team-member';

/**
 * 클라이언트 측 클래스: 팀 구성원 변경 (Mutations)
 * API 라우트를 통해 클라이언트 컴포넌트에서 사용됨
 */
class TeamService {
  /**
   * 새 구성원 초대 (API 라우트를 통한 클라이언트 측 호출)
   */
  async inviteMember(
    businessId: string,
    data: InviteMemberRequest
  ): Promise<InviteMemberHandlerResponse> {
    const response = await fetch(`/api/business/${businessId}/member`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return response.json();
  }

  /**
   * 구성원 삭제 (API 라우트를 통한 클라이언트 측 호출)
   */
  async deleteMember(
    businessId: string,
    userId: string
  ): Promise<DeleteMemberHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/member/${userId}`,
      {
        method: 'DELETE',
      }
    );

    return response.json();
  }

  /**
   * 구성원 권한 변경 (API 라우트를 통한 클라이언트 측 호출)
   */
  async changeMemberRole(
    businessId: string,
    userId: string,
    newRole: BusinessRole
  ): Promise<ChangeMemberRoleHandlerResponse> {
    const body: ChangeMemberRoleRequest = { newRole };

    const response = await fetch(
      `/api/business/${businessId}/member/${userId}/role`,
      {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      }
    );

    return response.json();
  }

  /**
   * 구성원 활성화 (API 라우트를 통한 클라이언트 측 호출)
   */
  async activateMember(
    businessId: string,
    userId: string
  ): Promise<MemberStatusChangeHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/member/${userId}/activate`,
      {
        method: 'PATCH',
      }
    );

    return response.json();
  }

  /**
   * 구성원 비활성화 (API 라우트를 통한 클라이언트 측 호출)
   */
  async deactivateMember(
    businessId: string,
    userId: string
  ): Promise<MemberStatusChangeHandlerResponse> {
    const response = await fetch(
      `/api/business/${businessId}/member/${userId}/deactivate`,
      {
        method: 'PATCH',
      }
    );

    return response.json();
  }
}

export const teamService = new TeamService();
