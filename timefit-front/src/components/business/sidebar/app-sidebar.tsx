'use client';

import * as React from 'react';

import type { AppSidebarProps } from '@/types/sidebar/app-sidebar';
import { NAV_ITEMS } from '@/lib/data/sidebar/sidebar-menu';
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
        <NavMain items={NAV_ITEMS} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  );
}
