import { TableBody } from '@/components/ui/table';
import { TeamTableRow, type TeamMember } from './team-table-row';

interface TeamTableBodyProps {
  members: TeamMember[];
  onChangeRole?: (memberId: string) => void;
  onChangeStatus?: (memberId: string) => void;
  onDelete?: (memberId: string) => void;
}

export function TeamTableBody({
  members,
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
          onChangeRole={onChangeRole}
          onChangeStatus={onChangeStatus}
          onDelete={onDelete}
        />
      ))}
    </TableBody>
  );
}
