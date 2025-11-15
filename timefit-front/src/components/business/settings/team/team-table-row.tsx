import { TableCell, TableRow } from '@/components/ui/table';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { TeamActionsDropdown } from './team-actions-dropdown';

export interface TeamMember {
  id: string;
  name: string;
  email: string;
  role: 'OWNER' | 'MANAGER' | 'MEMBER';
  joinDate: string;
  status: 'active' | 'invited' | 'inactive';
  avatar?: string;
}

interface TeamTableRowProps {
  member: TeamMember;
  onChangeRole?: (memberId: string) => void;
  onChangeStatus?: (memberId: string) => void;
  onDelete?: (memberId: string) => void;
}

const getRoleBadgeVariant = (role: TeamMember['role']) => {
  switch (role) {
    case 'OWNER':
      return 'destructive' as const;
    case 'MANAGER':
      return 'default' as const;
    case 'MEMBER':
      return 'secondary' as const;
    default:
      return 'outline' as const;
  }
};

const getStatusBadgeVariant = (status: TeamMember['status']) => {
  switch (status) {
    case 'active':
      return 'default' as const;
    case 'invited':
      return 'outline' as const;
    case 'inactive':
      return 'secondary' as const;
    default:
      return 'outline' as const;
  }
};

const getRoleLabel = (role: TeamMember['role']) => {
  switch (role) {
    case 'OWNER':
      return '관리자';
    case 'MANAGER':
      return '매니저';
    case 'MEMBER':
      return '멤버(직원)';
    default:
      return role;
  }
};

const getStatusLabel = (status: TeamMember['status']) => {
  switch (status) {
    case 'active':
      return '활성';
    case 'invited':
      return '초대중';
    case 'inactive':
      return '비활성';
    default:
      return status;
  }
};

export function TeamTableRow({
  member,
  onChangeRole,
  onChangeStatus,
  onDelete,
}: TeamTableRowProps) {
  return (
    <TableRow>
      <TableCell>
        <div className="flex items-center gap-3">
          <Avatar className="h-8 w-8">
            <AvatarImage src={member.avatar} alt={member.name} />
            <AvatarFallback>{member.name.charAt(0)}</AvatarFallback>
          </Avatar>
          <span className="font-medium">{member.name}</span>
        </div>
      </TableCell>
      <TableCell className="text-muted-foreground">{member.email}</TableCell>
      <TableCell>
        <Badge variant={getRoleBadgeVariant(member.role)}>
          {getRoleLabel(member.role)}
        </Badge>
      </TableCell>
      <TableCell className="text-muted-foreground">{member.joinDate}</TableCell>
      <TableCell>
        <Badge variant={getStatusBadgeVariant(member.status)}>
          {getStatusLabel(member.status)}
        </Badge>
      </TableCell>
      <TableCell className="text-right">
        <TeamActionsDropdown
          memberId={member.id}
          onChangeRole={onChangeRole}
          onChangeStatus={onChangeStatus}
          onDelete={onDelete}
        />
      </TableCell>
    </TableRow>
  );
}
