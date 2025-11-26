import { TableBody } from '@/components/ui/table';

import { TeamTableRow, type TeamMember } from './team-table-row';

interface TeamTableBodyProps {
  members: TeamMember[];
  currentUserId?: string;
  isOwner?: boolean;
  onChangeRole?: (memberId: string) => void;
  onChangeStatus?: (memberId: string) => void;
  onDelete?: (memberId: string) => void;
}

export function TeamTableBody({
  members,
  currentUserId,
  isOwner = false,
  onChangeRole,
  onChangeStatus,
  onDelete,
}: TeamTableBodyProps) {
  return (
    <TableBody>
      {members.map(member => (
        <TeamTableRow
          key={member.id}
          member={member}
          currentUserId={currentUserId}
          isOwner={isOwner}
          onChangeRole={onChangeRole}
          onChangeStatus={onChangeStatus}
          onDelete={onDelete}
        />
      ))}
    </TableBody>
  );
}
