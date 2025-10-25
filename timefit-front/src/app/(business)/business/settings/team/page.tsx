'use client';

import { Card, CardContent } from '@/components/ui/card';
import { Table } from '@/components/ui/table';
import { TeamHeader } from '@/components/business/settings/team/team-header';
import { TeamTableHeader } from '@/components/business/settings/team/team-table-header';
import { TeamTableBody } from '@/components/business/settings/team/team-table-body';
import type { TeamMember } from '@/components/business/settings/team/team-table-row';

const mockTeamMembers: TeamMember[] = [
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

export default function Page() {
  const handleChangeRole = (memberId: string) => {
    console.log('Change role for member:', memberId);
  };

  const handleChangeStatus = (memberId: string) => {
    console.log('Change status for member:', memberId);
  };

  const handleDelete = (memberId: string) => {
    console.log('Delete member:', memberId);
  };

  return (
    <div>
      <Card>
        <TeamHeader title="팀 구성원" />
        <CardContent>
          <Table>
            <TeamTableHeader />
            <TeamTableBody
              members={mockTeamMembers}
              onChangeRole={handleChangeRole}
              onChangeStatus={handleChangeStatus}
              onDelete={handleDelete}
            />
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
