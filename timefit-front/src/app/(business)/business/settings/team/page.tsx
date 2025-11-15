'use client';

import { useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Table } from '@/components/ui/table';
import { UserPlus, Loader2, AlertCircle } from 'lucide-react';
import { TeamTableHeader } from '@/components/business/settings/team/team-table-header';
import { TeamTableBody } from '@/components/business/settings/team/team-table-body';
import {
  InviteMemberDialog,
  type InviteMemberData,
} from '@/components/business/settings/team/invite-member-dialog';
import { ChangeRoleDialog } from '@/components/business/settings/team/change-role-dialog';
import { ChangeStatusDialog } from '@/components/business/settings/team/change-status-dialog';
import type { TeamMember } from '@/components/business/settings/team/team-table-row';
import { useTeamMembers } from '@/hooks/business/useTeamMembers';
import { useBusinessStore } from '@/store/business-store';
import type { TeamMemberDetail } from '@/types/business/teamMember';

// 백엔드 TeamMemberDetail을 프론트엔드 TeamMember로 변환
const convertToTeamMember = (member: TeamMemberDetail): TeamMember => {
  // isActive에 따라 상태 결정
  const status: TeamMember['status'] = member.isActive ? 'active' : 'inactive';

  return {
    id: member.userId,
    name: member.name,
    email: member.email,
    role: member.role,
    joinDate: new Date(member.joinedAt).toLocaleDateString('ko-KR'),
    status,
  };
};

export default function Page() {
  const { business } = useBusinessStore();
  const businessId = business?.businessId || '';
  const { data, loading, error } = useTeamMembers(businessId);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [changeRoleDialogOpen, setChangeRoleDialogOpen] = useState(false);
  const [changeStatusDialogOpen, setChangeStatusDialogOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<TeamMember | null>(null);

  // 백엔드 데이터를 프론트엔드 형식으로 변환
  const teamMembers = data?.members.map(convertToTeamMember) || [];

  const handleChangeRole = (memberId: string) => {
    const member = teamMembers.find((m) => m.id === memberId);
    if (member) {
      setSelectedMember(member);
      setChangeRoleDialogOpen(true);
    }
  };

  const handleChangeStatus = (memberId: string) => {
    const member = teamMembers.find((m) => m.id === memberId);
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

  // 로딩 상태
  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  // 에러 상태
  if (error) {
    return (
      <div className="flex flex-col items-center justify-center gap-4 p-8">
        <AlertCircle className="h-12 w-12 text-destructive" />
        <p className="text-sm text-muted-foreground">{error}</p>
      </div>
    );
  }

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
              members={teamMembers}
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
