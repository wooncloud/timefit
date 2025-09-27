'use client';
import * as React from 'react';
import { Settings2, Store, User, House } from 'lucide-react';
import { NavMain } from '@/components/business/sidebar/nav-main';
import { NavUser } from '@/components/business/sidebar/nav-user';
import { TeamSwitcher } from '@/components/business/sidebar/team-switcher';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from '@/components/ui/sidebar';

const defaultUser = {
  name: '운구름',
  email: 'wooncloud@example.com',
  avatar: '/avatars/shadcn.jpg',
};

const defaultTeams = [
  { name: 'Acme Inc', plan: 'Enterprise' },
  { name: 'Acme Corp.', plan: 'Startup' },
  { name: 'Evil Corp.', plan: 'Free' },
];

const navItems = [
    {
      title: '메인',
      url: '#',
      icon: House,
      isActive: true,
      items: [
        // { title: "대시보드", url: "#" },
        { title: '캘린더', url: '#' },
      ],
    },
    {
      title: '업체 관리',
      url: '#',
      icon: Store,
      isActive: true,
      items: [
        { title: '영업 일정', url: '#' },
        { title: '서비스/상품', url: '#' },
        { title: '예약 현황', url: '#' },
      ],
    },
    {
      title: '고객 관리',
      url: '#',
      icon: User,
      isActive: true,
      items: [
        { title: '고객 목록', url: '#' },
        { title: '고객 채팅', url: '#' },
        { title: '공지', url: '#' },
      ],
    },
    {
      title: '설정',
      url: '#',
      icon: Settings2,
      isActive: true,
      items: [
        { title: '업체 정보', url: '#' },
        { title: '팀 관리', url: '#' },
      ],
    },
];

interface AppSidebarProps extends React.ComponentProps<typeof Sidebar> {
  user?: {
    name: string;
    email: string;
    avatar?: string | null;
  } | null;
  teams?: {
    name: string;
    plan: string;
  }[];
}

export function AppSidebar({ user, teams, ...props }: AppSidebarProps) {
  const sidebarUser = user ?? defaultUser;
  const sidebarTeams = teams ?? defaultTeams;

  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={sidebarTeams} />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={navItems} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={sidebarUser} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}
