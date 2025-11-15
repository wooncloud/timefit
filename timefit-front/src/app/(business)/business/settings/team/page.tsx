'use client';

import { useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Table } from '@/components/ui/table';
import { UserPlus } from 'lucide-react';
import { TeamTableHeader } from '@/components/business/settings/team/team-table-header';
import { TeamTableBody } from '@/components/business/settings/team/team-table-body';
import {
  InviteMemberDialog,
  type InviteMemberData,
} from '@/components/business/settings/team/invite-member-dialog';
import { ChangeRoleDialog } from '@/components/business/settings/team/change-role-dialog';
import { ChangeStatusDialog } from '@/components/business/settings/team/change-status-dialog';
import type { TeamMember } from '@/components/business/settings/team/team-table-row';
import { mockTeamMembers } from '@/lib/mock';

export default function Page() {
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [changeRoleDialogOpen, setChangeRoleDialogOpen] = useState(false);
  const [changeStatusDialogOpen, setChangeStatusDialogOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<TeamMember | null>(null);

  const handleChangeRole = (memberId: string) => {
    const member = mockTeamMembers.find((m) => m.id === memberId);
    if (member) {
      setSelectedMember(member);
      setChangeRoleDialogOpen(true);
    }
  };

  const handleChangeStatus = (memberId: string) => {
    const member = mockTeamMembers.find((m) => m.id === memberId);
    if (member) {
      // 초대중 상태인 경우 상태 변경 불가
      if (member.status === 'invited') {
        alert('초대중인 팀원은 상태를 변경할 수 없습니다.');
        return;
      }
      setSelectedMember(member);
      setChangeStatusDialogOpen(true);
    }
  };

  const handleDelete = (memberId: string) => {
    console.log('Delete member:', memberId);
    // TODO: 삭제 확인 다이얼로그 및 API 호출
  };

  const handleInvite = (data: InviteMemberData) => {
    console.log('Invite member:', data);
    // TODO: API 호출로 팀원 초대 로직 구현
  };

  const handleConfirmRoleChange = (
    memberId: string,
    newRole: TeamMember['role']
  ) => {
    console.log('Change role:', memberId, newRole);
    // TODO: API 호출로 권한 변경 로직 구현
  };

  const handleConfirmStatusChange = (
    memberId: string,
    newStatus: TeamMember['status']
  ) => {
    console.log('Change status:', memberId, newStatus);
    // TODO: API 호출로 상태 변경 로직 구현
  };

  return (
    <div>
      <Card>
        <CardContent className="pt-4">
          <div className="mb-4 flex justify-end">
            <Button onClick={() => setInviteDialogOpen(true)}>
              <UserPlus className="mr-2 h-4 w-4" />
              초대하기
            </Button>
          </div>

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

      <InviteMemberDialog
        open={inviteDialogOpen}
        onOpenChange={setInviteDialogOpen}
        onInvite={handleInvite}
      />

      <ChangeRoleDialog
        open={changeRoleDialogOpen}
        onOpenChange={setChangeRoleDialogOpen}
        member={selectedMember}
        onConfirm={handleConfirmRoleChange}
      />

      <ChangeStatusDialog
        open={changeStatusDialogOpen}
        onOpenChange={setChangeStatusDialogOpen}
        member={selectedMember}
        onConfirm={handleConfirmStatusChange}
      />
    </div>
  );
}
