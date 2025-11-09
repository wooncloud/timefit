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
const defaultTeamsMock = [
  { name: 'Acme Inc', plan: 'Enterprise' },
  { name: 'Acme Corp.', plan: 'Startup' },
  { name: 'Evil Corp.', plan: 'Free' },
];

export function AppSidebar({ teams, ...props }: AppSidebarProps) {
  const sidebarTeams = teams ?? defaultTeamsMock;

  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={sidebarTeams} />
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={navItems} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}
