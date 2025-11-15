/**
 * Team Member API Types
 * 비즈니스 구성원 관리 관련 타입 정의
 */

export type BusinessRole = 'OWNER' | 'MANAGER' | 'MEMBER';

export interface TeamMemberDetail {
  userId: string;
  email: string;
  name: string;
  role: BusinessRole;
  joinedAt: string; // ISO 8601 datetime string
  isActive: boolean;
  invitedByName: string;
  lastLoginAt: string; // ISO 8601 datetime string
}

export interface MemberListResponse {
  businessId: string;
  businessName: string;
  members: TeamMemberDetail[];
  totalCount: number;
}

// Backend API response wrapper
export interface GetTeamMembersApiResponse {
  data?: MemberListResponse;
  message?: string;
}

// Next.js handler response
export interface GetTeamMembersHandlerResponse {
  success: boolean;
  data?: MemberListResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
