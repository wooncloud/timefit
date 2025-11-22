// 비즈니스 구성원 역할 타입
export type BusinessRole = 'OWNER' | 'MANAGER' | 'MEMBER';

// 팀 구성원 상세 정보
export interface TeamMemberDetail {
  userId: string;
  email: string;
  name: string;
  role: BusinessRole;
  joinedAt: string;
  isActive: boolean;
  invitedByName: string;
  lastLoginAt: string;
}

// 구성원 목록 조회 응답
export interface MemberListResponse {
  businessId: string;
  businessName: string;
  members: TeamMemberDetail[];
  totalCount: number;
}

// 구성원 목록 조회 백엔드 API 응답
export interface GetTeamMembersApiResponse {
  data?: MemberListResponse;
  message?: string;
}

// 구성원 목록 조회 Next.js 핸들러 응답
export interface GetTeamMembersHandlerResponse {
  success: boolean;
  data?: MemberListResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

// 구성원 역할 변경 요청
export interface ChangeMemberRoleRequest {
  newRole: BusinessRole;
}

// 구성원 역할 변경 백엔드 API 응답
export interface ChangeMemberRoleApiResponse {
  data?: void;
  message?: string;
}

// 구성원 역할 변경 Next.js 핸들러 응답
export interface ChangeMemberRoleHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

// 구성원 활성화/비활성화 백엔드 API 응답
export interface MemberStatusChangeApiResponse {
  data?: void;
  message?: string;
}

// 구성원 활성화/비활성화 Next.js 핸들러 응답
export interface MemberStatusChangeHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

// 구성원 삭제 백엔드 API 응답
export interface DeleteMemberApiResponse {
  data?: void;
  message?: string;
}

// 구성원 삭제 Next.js 핸들러 응답
export interface DeleteMemberHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

// 구성원 초대 요청
export interface InviteMemberRequest {
  email: string;
  invitationMessage?: string;
}

// 구성원 초대 응답 데이터
export interface InviteMemberResponse {
  userId: string;
  email: string;
  name: string;
  role: BusinessRole;
  joinedAt: string;
  isActive: boolean;
  invitedByName: string | null;
  lastLoginAt: string | null;
}

// 구성원 초대 백엔드 API 응답
export interface InviteMemberApiResponse {
  data?: InviteMemberResponse;
  message?: string;
}

// 구성원 초대 Next.js 핸들러 응답
export interface InviteMemberHandlerResponse {
  success: boolean;
  data?: InviteMemberResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
