'use client';

import { Card, CardContent } from '@/components/ui/card';
import { Table } from '@/components/ui/table';
import { TeamTableHeader } from '@/components/business/settings/team/team-table-header';
import { TeamTableBody } from '@/components/business/settings/team/team-table-body';
import { mockTeamMembers } from '@/lib/mock';

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
        <CardContent className="pt-4">
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
