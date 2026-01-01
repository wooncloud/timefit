'use client';

import { useState } from 'react';
import { UserPlus } from 'lucide-react';
import { toast } from 'sonner';

import type {
  MemberListResponse,
  TeamMemberDetail,
} from '@/types/business/team-member';
import { useActivateMember } from '@/hooks/team/mutations/use-activate-member';
import { useChangeMemberRole } from '@/hooks/team/mutations/use-change-member-role';
import { useDeactivateMember } from '@/hooks/team/mutations/use-deactivate-member';
import { useDeleteMember } from '@/hooks/team/mutations/use-delete-member';
import { useInviteMember } from '@/hooks/team/mutations/use-invite-member';
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

interface TeamClientProps {
  initialMembers: MemberListResponse;
  businessId: string;
  currentUserId: string;
}

export function TeamClient({
  initialMembers,
  businessId,
  currentUserId,
}: TeamClientProps) {
  const { inviteMember } = useInviteMember(businessId);
  const { deleteMember } = useDeleteMember(businessId);
  const { changeMemberRole } = useChangeMemberRole(businessId);
  const { activateMember } = useActivateMember(businessId);
  const { deactivateMember } = useDeactivateMember(businessId);

  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [changeRoleDialogOpen, setChangeRoleDialogOpen] = useState(false);
  const [changeStatusDialogOpen, setChangeStatusDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<TeamMember | null>(null);

  // 백엔드 데이터를 프론트엔드 형식으로 변환
  const teamMembers = initialMembers.members.map(convertToTeamMember);

  // 현재 사용자의 역할 확인 (OWNER만 관리 권한)
  const currentUserMember = initialMembers.members.find(
    m => m.userId === currentUserId
  );
  const isOwner = currentUserMember?.role === 'OWNER';

  const refreshPage = () => {
    window.location.reload();
  };

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
      refreshPage();
    }
  };

  const handleInvite = async (data: InviteMemberData) => {
    const success = await inviteMember(data);
    if (success) {
      refreshPage();
    }
  };

  const handleConfirmRoleChange = async (
    memberId: string,
    newRole: TeamMember['role']
  ) => {
    const success = await changeMemberRole(memberId, newRole);
    if (success) {
      refreshPage();
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
      refreshPage();
    }
  };

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
