import type { TeamMember } from '@/components/business/settings/team/team-table-row';

export const mockTeamMembers: TeamMember[] = [
  {
    id: '1',
    name: '홍길동',
    email: 'hong@timefit.com',
    role: 'OWNER',
    joinDate: '2025-08-15',
    status: 'active',
  },
  {
    id: '2',
    name: '김매니저',
    email: 'manager@timefit.com',
    role: 'MANAGER',
    joinDate: '2025-08-20',
    status: 'inactive',
  },
  {
    id: '3',
    name: '이직원',
    email: 'member1@timefit.com',
    role: 'MEMBER',
    joinDate: '2025-09-01',
    status: 'active',
  },
  {
    id: '4',
    name: '박알바',
    email: 'parttime@timefit.com',
    role: 'MEMBER',
    joinDate: '2025-09-18',
    status: 'invited',
  },
];
