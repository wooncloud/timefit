'use client';

import { useState } from 'react';
import { AlertCircle, Loader2, UserPlus } from 'lucide-react';
import { toast } from 'sonner';

import type { TeamMemberDetail } from '@/types/business/team-member';
import { useTeamMembers } from '@/hooks/business/use-team-members';
import { useBusinessStore } from '@/store/business-store';
import { useUserStore } from '@/store/user-store';
import { ChangeRoleDialog } from '@/components/business/settings/team/change-role-dialog';
import { ChangeStatusDialog } from '@/components/business/settings/team/change-status-dialog';
import {
  InviteMemberDialog,
  type InviteMemberData,
} from '@/components/business/settings/team/invite-member-dialog';
import { TeamTableBody } from '@/components/business/settings/team/team-table-body';
import { TeamTableHeader } from '@/components/business/settings/team/team-table-header';
import type { TeamMember } from '@/components/business/settings/team/team-table-row';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { ConfirmDialog } from '@/components/ui/confirm-dialog';
import { Table } from '@/components/ui/table';

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
  const { user } = useUserStore();
  const businessId = business?.businessId || '';
  const currentUserId = user?.userId || '';
  const {
    data,
    loading,
    error,
    changeMemberRole,
    activateMember,
    deactivateMember,
    deleteMember,
    inviteMember,
  } = useTeamMembers(businessId);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [changeRoleDialogOpen, setChangeRoleDialogOpen] = useState(false);
  const [changeStatusDialogOpen, setChangeStatusDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<TeamMember | null>(null);

  // 백엔드 데이터를 프론트엔드 형식으로 변환
  const teamMembers = data?.members.map(convertToTeamMember) || [];

  // 현재 사용자의 역할 확인 (OWNER만 관리 권한)
  const currentUserMember = data?.members.find(m => m.userId === currentUserId);
  const isOwner = currentUserMember?.role === 'OWNER';

  const handleChangeRole = (memberId: string) => {
    const member = teamMembers.find(m => m.id === memberId);
    if (member) {
      setSelectedMember(member);
      setChangeRoleDialogOpen(true);
    }
  };

  const handleChangeStatus = (memberId: string) => {
    const member = teamMembers.find(m => m.id === memberId);
    if (member) {
      // 초대중 상태인 경우 상태 변경 불가
      if (member.status === 'invited') {
        toast.error('초대중인 팀원은 상태를 변경할 수 없습니다.');
        return;
      }
      setSelectedMember(member);
      setChangeStatusDialogOpen(true);
    }
  };

  const handleDelete = (memberId: string) => {
    const member = teamMembers.find(m => m.id === memberId);
    if (member) {
      setSelectedMember(member);
      setDeleteDialogOpen(true);
    }
  };

  const handleConfirmDelete = async () => {
    if (!selectedMember) return;

    const success = await deleteMember(selectedMember.id);
    if (success) {
      toast.success('구성원이 삭제되었습니다.');
    }
  };

  const handleInvite = async (data: InviteMemberData) => {
    await inviteMember(data);
  };

  const handleConfirmRoleChange = async (
    memberId: string,
    newRole: TeamMember['role']
  ) => {
    const success = await changeMemberRole(memberId, newRole);
    if (success) {
      toast.success('권한이 변경되었습니다.');
    }
  };

  const handleConfirmStatusChange = async (
    memberId: string,
    newStatus: TeamMember['status']
  ) => {
    const success =
      newStatus === 'active'
        ? await activateMember(memberId)
        : await deactivateMember(memberId);

    if (success) {
      toast.success('상태가 변경되었습니다.');
    }
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
          {isOwner && (
            <div className="mb-4 flex justify-end">
              <Button onClick={() => setInviteDialogOpen(true)}>
                <UserPlus className="mr-2 h-4 w-4" />
                초대하기
              </Button>
            </div>
          )}

          <Table>
            <TeamTableHeader />
            <TeamTableBody
              members={teamMembers}
              currentUserId={currentUserId}
              isOwner={isOwner}
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

      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        title="구성원 삭제"
        description={`정말로 ${selectedMember?.name}님을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`}
        confirmText="삭제"
        cancelText="취소"
        variant="destructive"
        onConfirm={handleConfirmDelete}
      />
    </div>
  );
}
