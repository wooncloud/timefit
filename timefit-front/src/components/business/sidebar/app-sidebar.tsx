'use client';
import * as React from 'react';

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
import type { AppSidebarProps } from '@/types/sidebar/appSidebar';
import { navItems } from '@/lib/data/sidebar/sidebarMenu';

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
